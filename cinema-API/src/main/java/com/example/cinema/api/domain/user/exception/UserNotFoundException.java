package com.example.cinema.api.domain.user.exception;

import com.example.cinema.api.domain.exception.ResourceNotFoundException;

public class UserNotFoundException extends ResourceNotFoundException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
