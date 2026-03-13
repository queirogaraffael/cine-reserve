package com.example.cinema.api.infrastructure.security.exception;

import com.example.cinema.api.shared.exception.validation;

public class RefreshTokenInvalidException extends validation {
    public RefreshTokenInvalidException(String message) {
        super(message);
    }
}
