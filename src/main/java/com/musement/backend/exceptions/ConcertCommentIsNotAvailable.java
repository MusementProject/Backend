package com.musement.backend.exceptions;

public class ConcertCommentIsNotAvailable extends RuntimeException {
    public ConcertCommentIsNotAvailable(Long userId, Long concertId) {
        super("User with id: " + userId + " is not attend concert with id: " + concertId + "\n");
    }
}
