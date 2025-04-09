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
public class ExternalIds {
    @JsonProperty("isrc")
    private String isrc;
    @JsonProperty("ean")
    private String ean;
    @JsonProperty("ups")
    private String ups;

}
