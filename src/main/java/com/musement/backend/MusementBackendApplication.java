package com.musement.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = "com.musement.backend")
@EnableTransactionManagement
public class MusementBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(MusementBackendApplication.class, args);
    }

}
