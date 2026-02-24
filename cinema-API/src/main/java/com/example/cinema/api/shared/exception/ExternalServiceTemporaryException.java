package com.example.cinema.api.shared.exception;

public class ExternalServiceTemporaryException extends RuntimeException {
    public ExternalServiceTemporaryException(String message) {
        super(message);
    }
}
