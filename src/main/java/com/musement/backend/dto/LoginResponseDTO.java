package com.musement.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDTO {
    // TODO JWT ???
    // private String token;

    private Long id;
    private String username;
    private String email;
}
