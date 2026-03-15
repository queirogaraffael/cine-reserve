package com.example.cinema.api.domain.user.exception;

import com.example.cinema.api.domain.exception.StateConflictException;

public class UserAlreadyExistsException extends StateConflictException {

    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
