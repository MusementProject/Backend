package com.musement.backend.controllers;

import com.musement.backend.dto.ArtistStatisticsDTO;
import com.musement.backend.dto.SpotifyPlaylistRequest;
import com.musement.backend.dto.PlaylistResponseDTO;
import com.musement.backend.dto.PlaylistInfoDTO;
import com.musement.backend.models.Playlist;
import com.musement.backend.models.PlaylistArtistStat;
import com.musement.backend.models.User;
import com.musement.backend.repositories.PlaylistRepository;
import com.musement.backend.repositories.UserRepository;
import com.musement.backend.services.SpotifyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/playlists")
public class SpotifyController {

    private final SpotifyService getPlaylistSpotifyService;
    private final UserRepository userRepository;
    private final PlaylistRepository playlistRepository;

    public SpotifyController(SpotifyService getPlaylistSpotifyService, UserRepository userRepository, PlaylistRepository playlistRepository) {
        this.getPlaylistSpotifyService = getPlaylistSpotifyService;
        this.userRepository = userRepository;
        this.playlistRepository = playlistRepository;
    }

    @PostMapping("/add")
    public PlaylistResponseDTO addPlaylist(@Valid @RequestBody SpotifyPlaylistRequest request) {
        getPlaylistSpotifyService.calculateArtistStatistics(request.getUserId(), request.getPlaylistId(), request.getPlaylistTitle())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Playlist not found"));

        User user = userRepository.findById(request.getUserId()).orElseThrow();
        Playlist playlist = user.getPlaylists().stream()
                .filter(p -> p.getPlaylistUrl().equals(request.getPlaylistId()) && p.getTitle().equals(request.getPlaylistTitle()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Playlist not saved"));
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
