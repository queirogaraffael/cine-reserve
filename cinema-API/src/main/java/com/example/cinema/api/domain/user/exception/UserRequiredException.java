package com.example.cinema.api.domain.user.exception;

import com.example.cinema.api.domain.exception.ValidationException;

public class UserRequiredException extends ValidationException {
    public UserRequiredException(String message) {
        super(message);
    }
}
