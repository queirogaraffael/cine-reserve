package com.example.cinema.api.domain.user.exception;

import com.example.cinema.api.domain.exception.ValidationException;

public class InvalidPasswordException extends ValidationException {
    public InvalidPasswordException(String message) {
        super(message);
    }
}
