package com.musement.backend.controllers;

import com.musement.backend.dto.PlaylistInfoDTO;
import com.musement.backend.dto.PlaylistResponseDTO;
import com.musement.backend.dto.PlaylistResponseDTO;
import com.musement.backend.models.Playlist;
import com.musement.backend.models.PlaylistArtistStat;
import com.musement.backend.models.User;
import com.musement.backend.repositories.PlaylistRepository;
import com.musement.backend.repositories.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/playlists")
public class PlaylistController {
    private final UserRepository userRepository;
    private final PlaylistRepository playlistRepository;

    public PlaylistController(UserRepository userRepository, PlaylistRepository playlistRepository) {
        this.userRepository = userRepository;
        this.playlistRepository = playlistRepository;
    }

    // все плейлисты для ленты плейлистов
    @GetMapping("/user/{userId}")
    public List<PlaylistResponseDTO> getUserPlaylists(@PathVariable Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        List<PlaylistResponseDTO> result = new ArrayList<>();
        for (Playlist playlist : user.getPlaylists()) {
            result.add(toPlaylistResponseDTO(playlist));
        }
        return result;
    }

    // конкретный плейлист
    @GetMapping("/{playlistId}/stats")
    public PlaylistResponseDTO getPlaylistStats(@PathVariable Long playlistId) {
        Playlist playlist = playlistRepository.findById(playlistId).orElseThrow();
        return toPlaylistResponseDTO(playlist);
    }

    private PlaylistResponseDTO toPlaylistResponseDTO(Playlist playlist) {
        PlaylistResponseDTO dto = new PlaylistResponseDTO();
        dto.setPlaylistId(playlist.getId());
        dto.setPlaylistUrl(playlist.getPlaylistUrl());
        dto.setTitle(playlist.getTitle());
        List<PlaylistInfoDTO> infoList = new ArrayList<>();
        for (PlaylistArtistStat stat : playlist.getArtistStats()) {
            PlaylistInfoDTO info = new PlaylistInfoDTO();
            info.setArtistId(stat.getArtist().getId());
            info.setArtist(stat.getArtist());
            info.setCount(stat.getTrackCount());
            infoList.add(info);
        }
        infoList.sort((a, b) -> b.getCount() - a.getCount());
        dto.setPlaylistInfo(infoList);
        return dto;
    }
} 