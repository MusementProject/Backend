package com.musement.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SpotifyPlaylistRequest {
    @NotNull
    Long userId;
    @NotBlank(message = "PlaylistId is mandatory")
    String playlistId;
    @NotBlank(message = "PlaylistTitle is mandatory")
    String playlistTitle;
}
