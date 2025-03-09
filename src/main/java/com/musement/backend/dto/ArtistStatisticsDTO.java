package com.musement.backend.dto;

import com.musement.backend.models.Artist;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArtistStatisticsDTO {
    private Long userId;
    private Long artistId;
    private Artist artist;
    private Integer counter;
}
