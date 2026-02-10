package com.example.cinema.api.shared.exceptions;

public class ExternalServiceTemporaryException extends RuntimeException {
    public ExternalServiceTemporaryException(String message) {
        super(message);
    }
}
