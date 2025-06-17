package com.musement.backend.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class ConcertDTO {
    private Long id;
    private Long artistId;
    private String artistName;
    private String imageUrl;
    private String location;
    private LocalDateTime date;
} 