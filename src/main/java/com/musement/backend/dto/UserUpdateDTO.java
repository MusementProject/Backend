package com.musement.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateDTO {
    private String username;
    private String email;
    private String bio;
    private String fullName;
    private String profilePicture;
}
