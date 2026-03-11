package com.example.cinema.api.infrastructure.security.exception;

import com.example.cinema.api.shared.exception.InternalServerErrorException;

public class TokenCreationException extends InternalServerErrorException {
    public TokenCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
