package com.musement.backend.exceptions;

public class SpotifyServerException extends RuntimeException {
  public SpotifyServerException(String message) {
    super(message);
  }
}
