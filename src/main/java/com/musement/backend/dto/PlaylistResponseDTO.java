package com.musement.backend.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class PlaylistResponseDTO {
    private Long playlistId;
    private String playlistUrl;
    private String title;
    private List<PlaylistInfoDTO> playlistInfo;
}