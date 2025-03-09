package com.musement.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationRequestDTO {
    private String username;
    private String email;
    private String password;
}
