package com.musement.backend.dto;

import com.musement.backend.models.Artist;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistFromSpotifyStatisticsDTO {
    private String name;
    private Map<Artist, Integer> playlist_statistics;
}
