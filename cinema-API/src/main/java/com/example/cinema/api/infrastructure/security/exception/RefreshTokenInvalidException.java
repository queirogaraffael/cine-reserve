package com.example.cinema.api.infrastructure.security.exception;

import com.example.cinema.api.shared.exception.BadRequestException;

public class RefreshTokenInvalidException extends BadRequestException {
    public RefreshTokenInvalidException(String message) {
        super(message);
    }
}
