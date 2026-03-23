package com.example.cinema.api.domain.user.exception;

import com.example.cinema.api.domain.exception.StateConflictException;

public class PasswordReuseException extends StateConflictException {
    public PasswordReuseException(String message) {
        super(message);
    }
}
