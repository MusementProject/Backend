package com.musement.backend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "spotify")
@Component
@Getter
@Setter
public class SpotifyConfig {
    private String clientId;
    private String clientSecret;
}
