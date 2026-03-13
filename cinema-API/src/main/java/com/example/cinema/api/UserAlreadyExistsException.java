package com.example.cinema.api;

import com.example.cinema.api.domain.exception.StateConflictException;

public class UserAlreadyExistsException extends StateConflictException {

    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
