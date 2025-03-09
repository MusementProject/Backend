package com.musement.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RegistrationResponseDTO {
    private Long id;
    private String username;
    private String email;
}
