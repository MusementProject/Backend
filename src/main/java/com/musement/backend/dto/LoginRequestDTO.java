package com.musement.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDTO {
    @NotBlank(message = "Username is mandatory")
    private String username;
    @NotBlank(message = "Password is mandatory")
    private String password;

    public LoginRequestDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public LoginRequestDTO() {

    }
}
