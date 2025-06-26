package com.musement.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FriendConcertDTO {
    private Long userId;
    private String username;
    private String profileImageUrl;
    private boolean isAttending;
    private boolean isWishlisted;
    
    public FriendConcertDTO(Long userId, String username, String profileImageUrl, boolean isAttending, boolean isWishlisted) {
        this.userId = userId;
        this.username = username;
        this.profileImageUrl = profileImageUrl;
        this.isAttending = isAttending;
        this.isWishlisted = isWishlisted;
    }
} 