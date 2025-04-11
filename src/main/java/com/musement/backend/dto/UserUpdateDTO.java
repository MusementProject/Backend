package com.musement.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateDTO {
    private String username;
    private String nickname;
    private String bio;
    private String profilePicture;
    private String telegram;
}
