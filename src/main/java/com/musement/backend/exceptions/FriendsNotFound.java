package com.musement.backend.exceptions;

public class FriendsNotFound extends RuntimeException {
    public FriendsNotFound(Long userId, Long friendId) {
        super("User " + userId + " and User " + friendId + " are not friends");
    }
}
