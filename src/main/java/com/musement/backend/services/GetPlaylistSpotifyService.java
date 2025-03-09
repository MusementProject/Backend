package com.musement.backend.services;

import com.musement.backend.dto.ArtistStatisticsDTO;
import com.musement.backend.dto.PlaylistFromSpotifyDTO;
import com.musement.backend.models.Artist;
import com.musement.backend.models.ArtistStatistics;
import com.musement.backend.models.User;
import com.musement.backend.repositories.ArtistStatisticsRepository;
import com.musement.backend.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

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

    public GetPlaylistSpotifyService(
            ArtistService artistService,
            UserService userService,
            UserRepository userRepository,
            ArtistStatisticsRepository artistStatisticsRepository) {
        this.artistService = artistService;
        this.userService = userService;
        this.userRepository = userRepository;
        this.artistStatisticsRepository = artistStatisticsRepository;
    }

    /**
     * Обращается к Spotify API и получает плейлист по ссылке
     *
     * @param playlistUrl - ссылка на плейлист
     * @return объект PlaylistFromSpotifyDTO:
     * - title: название плейлиста
     * - artist: список исполнителей (каждый встречается столько раз, сколько его песен в плейлисте,
     * т.е. надо просто из джейсона вытащить из каждого трека исполнителя)
     */
    public PlaylistFromSpotifyDTO getPlaylistFromSpotify(String playlistUrl) {
        // TODO julia?

        // пример
        PlaylistFromSpotifyDTO dto = new PlaylistFromSpotifyDTO();
        dto.setTitle("title");

        String artistName = "artist";
        List<Artist> artists = new ArrayList<>();
        Artist artist = new Artist();

        // ищем артиста в бд, если его нет - создаем
        Long artistId = artistService.findOrCreateArtist(artistName).getId();
        artist.setId(artistId);

        artist.setName(artistName);
        artists.add(artist);
        dto.setArtists(artists);

        return dto;
    }

    /**
     * Подсчитывает количество вхождений каждого артиста в плейлисте
     * и обновляет агрегированную статистику для пользователя.
     *
     * @param playlistUrl Ссылка на плейлист в Spotify.
     * @param userId      Идентификатор пользователя.
     * @return Список с данными: идентификатор артиста, имя и счетчик вхождений.
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
