package com.musement.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationRequestDTO {
    private String login;
    private String username;
    private String email;
    private String password;
}
