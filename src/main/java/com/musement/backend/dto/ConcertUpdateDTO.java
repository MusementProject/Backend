package com.musement.backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ConcertUpdateDTO {
    private String title;
    private String location;
    private LocalDateTime dateTime;
    private Long artistId;
}
