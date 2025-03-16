package com.musement.backend.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SpotifyAuthInfo {
    private String access_token;
    private String token_type;
    private int expires_in;
}
