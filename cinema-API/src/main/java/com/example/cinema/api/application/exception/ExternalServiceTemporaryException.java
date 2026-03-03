package com.example.cinema.api.application.exception;

public class ExternalServiceTemporaryException extends RuntimeException {
    public ExternalServiceTemporaryException(String message) {
        super(message);
    }
}
