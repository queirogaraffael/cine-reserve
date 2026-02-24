package com.example.cinema.api.application.exception;

public class TokenCreationException extends RuntimeException {
    public TokenCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
