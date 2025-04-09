package com.musement.backend.controllers;

import com.musement.backend.dto.ArtistStatisticsDTO;
import com.musement.backend.services.SpotifyService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/playlists")
public class SpotifyController {

    private final SpotifyService getPlaylistSpotifyService;

    public SpotifyController(SpotifyService getPlaylistSpotifyService) {
        this.getPlaylistSpotifyService = getPlaylistSpotifyService;
    }

    @GetMapping("/add/{userId}/{playlistId}/{playlistTitle}")
    public List<ArtistStatisticsDTO> getPlaylist(@PathVariable Long userId, @PathVariable String playlistId, @PathVariable String playlistTitle) {
        return getPlaylistSpotifyService.calculateArtistStatistics(userId, playlistId, playlistTitle)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Playlist not found"));
    }
}
