package com.musement.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class TicketDto {
    private Long id;
    private Long concertId;
    private String concertArtist;
    private String concertLocation;
    private Instant concertDate;
    private String fileUrl;
    private String fileFormat;
    private Instant uploadedAt;
}
