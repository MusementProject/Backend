package com.musement.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaylistInfoDTO {
    private Long artistId;
    private ArtistDTO artist;
    private int count;
} 