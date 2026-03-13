package com.example.cinema.api;

import com.example.cinema.api.domain.exception.StateConflictException;

public class UserNotAuthenticatedException extends StateConflictException {
    public UserNotAuthenticatedException(String message) {
        super(message);
    }
}
