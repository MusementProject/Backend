package com.musement.backend.exceptions;

public class WrondPasswordException extends RuntimeException {
    public WrondPasswordException(String message) {
        super(message);
    }
}
