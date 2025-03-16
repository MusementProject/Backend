package com.musement.backend.dto.SpotifyInfo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlaylistTrackObject {
    @JsonProperty("added_at")
    private String addedAt;
    @JsonProperty("added_by")
    private SpotifyUser addedBy;
    @JsonProperty("is_local")
    private boolean isLocal;
    @JsonProperty("track")
    private Track track;
}
