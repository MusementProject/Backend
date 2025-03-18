package com.musement.backend.exceptions;

public class ExpiredSpotifyTokenException extends RuntimeException {
    public ExpiredSpotifyTokenException() {
        super("Spotify Auth Token is expired");
    }
}
