package com.musement.backend.controllers;

import com.musement.backend.dto.ArtistStatisticsDTO;
import com.musement.backend.dto.SpotifyPlaylistRequest;
import com.musement.backend.services.SpotifyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/playlists")
public class SpotifyController {

    private final SpotifyService getPlaylistSpotifyService;

    public SpotifyController(SpotifyService getPlaylistSpotifyService) {
        this.getPlaylistSpotifyService = getPlaylistSpotifyService;
    }

    @PostMapping("/add")
    public List<ArtistStatisticsDTO> getPlaylist(@Valid @RequestBody SpotifyPlaylistRequest request) {
        return getPlaylistSpotifyService.calculateArtistStatistics(request.getUserId(), request.getPlaylistId(), request.getPlaylistTitle())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Playlist not found"));
    }
}
