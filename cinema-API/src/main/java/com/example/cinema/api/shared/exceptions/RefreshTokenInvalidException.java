package com.example.cinema.api.shared.exceptions;

public class RefreshTokenInvalidException extends RuntimeException {
    public RefreshTokenInvalidException(String message) {
        super(message);
    }
}
