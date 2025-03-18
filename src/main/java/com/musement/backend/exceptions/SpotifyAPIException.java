package com.musement.backend.exceptions;

public class SpotifyAPIException extends RuntimeException {
    public SpotifyAPIException(String message) {
        super(message);
    }
}
