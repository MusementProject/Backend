package com.musement.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    // TODO JWT ???
    private Long id;
    private String username;
    private String email;
    private String nickname;
    private String token;
}
