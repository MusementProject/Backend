package com.musement.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArtistDTO {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
} 