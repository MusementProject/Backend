package com.musement.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArtistUpdateDTO {
    private String name;
    private String description;
    private String imageUrl;

}
