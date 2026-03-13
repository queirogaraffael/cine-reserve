package com.example.cinema.api.infrastructure.security.exception;

import com.example.cinema.api.shared.exception.InternalServerError;

public class TokenCreationException extends InternalServerError {
    public TokenCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
