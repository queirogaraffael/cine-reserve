package com.example.cinema.api.domain.user.exception;

import com.example.cinema.api.domain.exception.ValidationException;

public class UserUnderageException extends ValidationException {
    public UserUnderageException(String message) {
        super(message);
    }
}
