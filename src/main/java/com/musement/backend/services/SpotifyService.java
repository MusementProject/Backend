package com.musement.backend.services;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.musement.backend.config.SpotifyConfig;
import com.musement.backend.dto.ArtistStatisticsDTO;
import com.musement.backend.dto.PlaylistFromSpotifyDTO;
import com.musement.backend.dto.SpotifyInfo.Playlist;
import com.musement.backend.dto.SpotifyInfo.PlaylistTrackObject;
import com.musement.backend.exceptions.ExpiredSpotifyTokenException;
import com.musement.backend.exceptions.SpotifyAPIException;
import com.musement.backend.exceptions.SpotifyServerException;
import com.musement.backend.exceptions.UserNotFoundException;
import com.musement.backend.models.Artist;
import com.musement.backend.models.ArtistStatistics;
import com.musement.backend.models.PlaylistArtistStat;
import com.musement.backend.models.User;
import com.musement.backend.repositories.ArtistStatisticsRepository;
import com.musement.backend.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
public class SpotifyService {
    private final ArtistService artistService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final ArtistStatisticsRepository artistStatisticsRepository;
    private final WebClient webClient;
    private final SpotifyConfig config;
    private final SpotifyAuthInfo authInfo;


    @Getter
    @Setter
    @Component
    public static class SpotifyAuthInfo {
        @JsonProperty("access_token")
        private String accessToken;
        @JsonProperty("token_type")
        private String tokenType;
        @JsonProperty("expires_in")
        private int expiresIn;
    }


    public SpotifyService(
            ArtistService artistService,
            UserService userService,
            UserRepository userRepository,
            ArtistStatisticsRepository artistStatisticsRepository,
            WebClient webClient,
            SpotifyConfig config) {
        this.artistService = artistService;
        this.userService = userService;
        this.userRepository = userRepository;
        this.artistStatisticsRepository = artistStatisticsRepository;
        this.webClient = webClient;
        this.config = config;
        this.authInfo = new SpotifyAuthInfo();
    }

    /**
     * Обращается к Spotify API и получает плейлист по ссылке
     *
     * @param playlistId - ссылка на плейлист
     * @return объект PlaylistFromSpotifyDTO:
     * - title (String): название плейлиста
     * - artist (Artist): список исполнителей (каждый встречается столько раз, сколько его песен в плейлисте,
     * т.е. надо просто из джейсона вытащить из каждого трека исполнителя)
     */
    public Optional<PlaylistFromSpotifyDTO> getPlaylistFromSpotify(String playlistId, String playlistTitle) {
        Optional<Playlist> response = getPlaylistInfo(playlistId);
        if (response.isEmpty()) {
            return Optional.empty();
        }
        Playlist playlist = response.get();
        PlaylistFromSpotifyDTO dto = new PlaylistFromSpotifyDTO();
        dto.setTitle(playlistTitle);
        List<Artist> artists = new ArrayList<>();
        for (PlaylistTrackObject trackObject : playlist.getItems()) {
            for (com.musement.backend.dto.SpotifyInfo.Artist artistSpotify : trackObject.getTrack().getArtists()) {
                String artistName = artistSpotify.getName();
                com.musement.backend.models.Artist artist = artistService.findOrCreateArtist(artistName);

                if (artist.getImageUrl() == null || artist.getImageUrl().isEmpty()) {
                    com.musement.backend.dto.SpotifyInfo.Artist fullArtist = fetchFullArtist(artistSpotify.getId());
                    if (fullArtist.getImages() != null && !fullArtist.getImages().isEmpty()) {
                        String imageUrl = fullArtist.getImages().get(0).getUrl();
                        artist.setImageUrl(imageUrl);
                        artistService.updateArtist(artist);
                    }
                }
                artists.add(artist);
            }
        }
        dto.setArtists(artists);

        return Optional.of(dto);
    }

    private Mono<Playlist> sendRequestForPlaylist(String playlistId) {
        return webClient
                .get()
                .uri("/v1/playlists/{playlistId}/tracks", playlistId)
                .header("Authorization", "Bearer " + authInfo.getAccessToken())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                    if (clientResponse.statusCode().equals(HttpStatusCode.valueOf(401))) {
                        throw new ExpiredSpotifyTokenException();
                    } else {
                        throw new SpotifyAPIException(clientResponse.toString());
                    }
                })
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> {
                    throw new SpotifyServerException(clientResponse.toString());
                })
                .bodyToMono(Playlist.class);
    }

    private com.musement.backend.dto.SpotifyInfo.Artist fetchFullArtist(String spotifyArtistId) {
        try {
            return webClient
                    .get()
                    .uri("/v1/artists/{id}", spotifyArtistId)
                    .header("Authorization", "Bearer " + authInfo.getAccessToken())
                    .retrieve()
                    .bodyToMono(com.musement.backend.dto.SpotifyInfo.Artist.class)
                    .block();
        } catch (Exception e) {
            return new com.musement.backend.dto.SpotifyInfo.Artist();
        }
    }

    public Optional<Playlist> getPlaylistInfo(String playlistId) {
        Playlist response;
        try {
            response = sendRequestForPlaylist(playlistId).block();
            return Optional.of(response);
        } catch (ExpiredSpotifyTokenException exception) {
            getNewAuthToken();
            return getPlaylistInfo(playlistId);
        } catch (SpotifyAPIException | SpotifyServerException exception) {
            return Optional.empty();
        }
    }

    public void getNewAuthToken() {
        String body = String.format(
                "grant_type=client_credentials&client_id=%s&client_secret=%s",
                config.getClientId(), config.getClientSecret()
        );

        SpotifyAuthInfo response = webClient
                .post()
                .uri("https://accounts.spotify.com/api/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(SpotifyAuthInfo.class).block();
        if (response != null) {
            authInfo.setAccessToken(response.getAccessToken());
            authInfo.setTokenType(response.getTokenType());
            authInfo.setExpiresIn(response.getExpiresIn());
        }
    }

    /**
     * Calculate artist statistics for the given playlist
     * and update the database.
     *
     * @param userId        User id.
     * @param playlistId    Link to the playlist.
     * @param playlistTitle playlist title
     * @return List of ArtistStatisticsDTO.
     */
    @Transactional
    public Optional<List<ArtistStatisticsDTO>> calculateArtistStatistics(Long userId, String playlistId, String playlistTitle) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Optional<PlaylistFromSpotifyDTO> playlistOpt = getPlaylistFromSpotify(playlistId, playlistTitle);
        if (playlistOpt.isEmpty()) {
            return Optional.empty();
        }
        PlaylistFromSpotifyDTO playlistInfo = playlistOpt.get();

        com.musement.backend.models.Playlist playlist = new com.musement.backend.models.Playlist();
        playlist.setTitle(playlistTitle);
        playlist.setOwner(user);
        playlist.setPlaylistUrl(playlistId);

        // кол-во песен каждого артиста
        Map<Long, PlaylistArtistStat> artistStatMap = new HashMap<>();
        for (Artist artist : playlistInfo.getArtists()) {
            PlaylistArtistStat stat = artistStatMap.get(artist.getId());
            if (stat == null) {
                stat = new PlaylistArtistStat();
                stat.setArtist(artist);
                stat.setPlaylist(playlist);
                stat.setTrackCount(1);
                artistStatMap.put(artist.getId(), stat);
            } else {
                stat.setTrackCount(stat.getTrackCount() + 1);
            }
        }
        playlist.setArtistStats(new HashSet<>(artistStatMap.values()));

        user.getPlaylists().add(playlist);
        userRepository.save(user);

        int totalTracks = playlistInfo.getArtists().size();

        // процент песен артиста
        Map<Long, Double> artistPercents = new HashMap<>();
        for (PlaylistArtistStat stat : artistStatMap.values()) {
            double percent = 100.0 * stat.getTrackCount() / totalTracks;
            artistPercents.put(stat.getArtist().getId(), percent);
        }

        // пересчет метрики
        for (PlaylistArtistStat stat : artistStatMap.values()) {
            Long artistId = stat.getArtist().getId();
            ArtistStatistics artistStatistics = artistStatisticsRepository.findByUserIdAndArtistId(userId, artistId);
            if (artistStatistics == null) {
                artistStatistics = new ArtistStatistics();
                artistStatistics.setUser(user);
                artistStatistics.setArtist(stat.getArtist());
                artistStatistics.setAveragePercent(artistPercents.get(artistId));
            } else {
                int count = getUserPlaylistCountWithArtist(user, stat.getArtist());
                double sumPercents = artistStatistics.getAveragePercent() * count;
                sumPercents += artistPercents.get(artistId);
                int newCount = count + 1;
                artistStatistics.setAveragePercent(sumPercents / newCount);
            }
            artistStatisticsRepository.save(artistStatistics);
        }

        List<ArtistStatisticsDTO> result = new ArrayList<>();
        for (PlaylistArtistStat stat : artistStatMap.values()) {
            ArtistStatisticsDTO dto = new ArtistStatisticsDTO();
            dto.setArtistId(stat.getArtist().getId());
            dto.setArtist(stat.getArtist());
            dto.setPercent(stat.getTrackCount() * 100.0 / totalTracks);
            result.add(dto);
        }
        return Optional.of(result);
    }

    // сколько плейлистов с конкретным артистом (для пересчета метрики)
    private int getUserPlaylistCountWithArtist(User user, Artist artist) {
        int count = 0;
        for (com.musement.backend.models.Playlist playlist : user.getPlaylists()) {
            for (PlaylistArtistStat stat : playlist.getArtistStats()) {
                if (stat.getArtist().getId().equals(artist.getId())) {
                    count++;
                    break;
                }
            }
        }
        return count;
    }
}
