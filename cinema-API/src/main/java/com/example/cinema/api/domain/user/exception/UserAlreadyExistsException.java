package com.example.cinema.api.domain.user.exception;

import com.example.cinema.api.shared.exception.ConflictException;

public class UserAlreadyExistsException extends ConflictException {

    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
