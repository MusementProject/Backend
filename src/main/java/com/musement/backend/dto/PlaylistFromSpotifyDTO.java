package com.musement.backend.dto;

import com.musement.backend.models.Artist;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class PlaylistFromSpotifyDTO {
    private String title;
    private List<Artist> artists;
}
