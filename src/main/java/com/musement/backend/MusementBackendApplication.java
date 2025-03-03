package com.musement.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.musement.backend")
public class MusementBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(MusementBackendApplication.class, args);
    }

}
