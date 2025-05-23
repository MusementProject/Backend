package com.musement.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FriendDTO {
    private Long id;
    private String username;
    private String nickname;
    private String profilePicture;
    private boolean accepted;
}
