package com.musement.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;

@Getter
@Setter
public class RegistrationRequestDTO {
    @NotBlank(message = "Login is mandatory")
    private String login;
    @NotBlank(message = "Username is mandatory")
    private String username;
    @NotBlank(message = "Email is mandatory")
    private String email;
    @NotBlank(message = "Password is mandatory")
    private String password;

    public RegistrationRequestDTO(String alice, String mail, String password) {
        this.login = alice;
        this.username = alice;
        this.email = mail;
        this.password = password;
    }

    public RegistrationRequestDTO() {
    }
}
