package com.musement.backend.controllers;

import com.musement.backend.dto.PlaylistFromSpotifyDTO;
import com.musement.backend.dto.SpotifyInfo.Playlist;
import com.musement.backend.services.GetPlaylistSpotifyService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/playlists")
public class GetPlaylistSpotifyController {

    private final GetPlaylistSpotifyService getPlaylistSpotifyService;

    public GetPlaylistSpotifyController(GetPlaylistSpotifyService getPlaylistSpotifyService){
        this.getPlaylistSpotifyService = getPlaylistSpotifyService;
    }

    @GetMapping("/add/{playlistId}")
    public PlaylistFromSpotifyDTO getPlaylist(@PathVariable("playlistId") String playlistId){
        return getPlaylistSpotifyService.getPlaylistFromSpotify(playlistId);
    }
}
