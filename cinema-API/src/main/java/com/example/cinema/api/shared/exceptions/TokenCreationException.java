package com.example.cinema.api.shared.exceptions;

public class TokenCreationException extends RuntimeException {
    public TokenCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
