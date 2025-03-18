package com.musement.backend.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class SpotifyAuthInfo {
    private String access_token;
    private String token_type;
    private int expires_in;
}
