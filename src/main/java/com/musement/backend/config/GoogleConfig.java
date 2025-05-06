package com.musement.backend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "google")
@Component
@Getter
@Setter
public class GoogleConfig {
    private String clientId = "572756897592-9o8mble3sva3bmu6mlgqdhtv1358jmn2.apps.googleusercontent.com";
}
