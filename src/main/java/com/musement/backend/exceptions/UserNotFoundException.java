package com.musement.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long userId) {
        super("User with id " + userId + " not found.");
    }
}

