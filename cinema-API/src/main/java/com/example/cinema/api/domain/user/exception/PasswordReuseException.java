package com.example.cinema.api.domain.user.exception;

import com.example.cinema.api.shared.exception.ConflictException;

public class PasswordReuseException extends ConflictException {
    public PasswordReuseException(String message) {
        super(message);
    }
}
