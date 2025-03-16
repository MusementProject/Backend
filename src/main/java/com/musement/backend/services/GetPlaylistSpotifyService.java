package com.musement.backend.services;

import com.musement.backend.dto.ArtistStatisticsDTO;
import com.musement.backend.dto.PlaylistFromSpotifyDTO;
import com.musement.backend.dto.SpotifyInfo.Playlist;
import com.musement.backend.dto.SpotifyInfo.PlaylistTrackObject;
import com.musement.backend.dto.SpotifyInfo.Track;
import com.musement.backend.models.Artist;
import com.musement.backend.models.ArtistStatistics;
import com.musement.backend.models.User;
import com.musement.backend.repositories.ArtistStatisticsRepository;
import com.musement.backend.repositories.UserRepository;
import com.musement.backend.util.SpotifyAuthInfo;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GetPlaylistSpotifyService {
    private final ArtistService artistService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final ArtistStatisticsRepository artistStatisticsRepository;
    private final WebClient webClient;

    public GetPlaylistSpotifyService(
            ArtistService artistService,
            UserService userService,
            UserRepository userRepository,
            ArtistStatisticsRepository artistStatisticsRepository, WebClient webClient) {
        this.artistService = artistService;
        this.userService = userService;
        this.userRepository = userRepository;
        this.artistStatisticsRepository = artistStatisticsRepository;
        this.webClient = webClient;
    }

    /**
     * Обращается к Spotify API и получает плейлист по ссылке
     *
     * @param playlistUrl - ссылка на плейлист
     * @return объект PlaylistFromSpotifyDTO:
     * - title (String): название плейлиста
     * - artist (Artist): список исполнителей (каждый встречается столько раз, сколько его песен в плейлисте,
     * т.е. надо просто из джейсона вытащить из каждого трека исполнителя)
     */
    public PlaylistFromSpotifyDTO getPlaylistFromSpotify(String playlistId) {
        Playlist playlist = getPlaylistInfo(playlistId);

        PlaylistFromSpotifyDTO dto = new PlaylistFromSpotifyDTO();
        dto.setTitle("title");
        List<Artist> artists = new ArrayList<>();
        for (PlaylistTrackObject trackObject : playlist.getItems()){
            for(com.musement.backend.dto.SpotifyInfo.Artist artistSpotify : trackObject.getTrack().getArtists()){
                String artistName = artistSpotify.getName();
                Artist artist = new Artist();

                Long artistId = artistService.findOrCreateArtist(artistName).getId();
                artist.setId(artistId);

                artist.setName(artistName);
                artists.add(artist);
            }
        }
        dto.setArtists(artists);

        return dto;
    }

    public Playlist getPlaylistInfo(String playlistId){

        SpotifyAuthInfo spotifyAuthInfo = getNewAuthToken().block();

        Playlist playlist = webClient
                .get()
                .uri("/v1/playlists/{playlistId}/tracks", playlistId)
                .header("Authorization", "Bearer " + spotifyAuthInfo.getAccess_token())
                .retrieve()
                .bodyToMono(Playlist.class).block();

        return playlist;
    }

    //"https://accounts.spotify.com/api/token"

    @Bean
    public Mono<SpotifyAuthInfo> getNewAuthToken(){
        return webClient
                .post()
                .uri("https://accounts.spotify.com/api/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue("grant_type=client_credentials&client_id=e189a81c4a2b411d8a7ed6ab4c4e272b&client_secret=57d7b488170f40f28ad6d0773805ba42") // переделайть!!!!
                .retrieve()
                .bodyToMono(SpotifyAuthInfo.class);
    }

    /**
     * Calculate artist statistics for the given playlist
     * and update the database.
     *
     * @param playlistUrl Link to the playlist.
     * @param userId      User id.
     * @return List of ArtistStatisticsDTO.
     */
    @Transactional
    public List<ArtistStatisticsDTO> calculateArtistStatistics(String playlistUrl, Long userId) {
        PlaylistFromSpotifyDTO playlist = getPlaylistFromSpotify(playlistUrl);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User with id " + userId + " not found."));

        // key - artistId, value - dto
        Map<Long, ArtistStatisticsDTO> artistStatisticsMap = new HashMap<>();

        for (Artist artist : playlist.getArtists()) {
            Long artistId = artist.getId();
            ArtistStatistics stat = artistStatisticsRepository.findByUserIdAndArtistId(userId, artistId);

            // if artist is not in the map for this user
            if (stat == null) {
                stat = new ArtistStatistics();
                stat.setArtist(artist);
                stat.setUser(user);
                stat.setCounter(1);

            } else {
                stat.setCounter(stat.getCounter() + 1);
            }

            artistStatisticsRepository.save(stat);

            // update the answer map
            ArtistStatisticsDTO dto = artistStatisticsMap.get(artistId);
            if (dto == null) {
                dto = new ArtistStatisticsDTO();
                dto.setArtistId(artistId);
                dto.setArtist(artist);
                dto.setCounter(stat.getCounter());
                dto.setUserId(userId);
                artistStatisticsMap.put(artistId, dto);
            } else {
                dto.setCounter(dto.getCounter() + 1);
            }
        }
        return new ArrayList<>(artistStatisticsMap.values());
    }
}
