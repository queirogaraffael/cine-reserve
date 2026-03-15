package com.example.cinema.api;

public class ExternalServiceTemporaryException extends RuntimeException {
    public ExternalServiceTemporaryException(String message) {
        super(message);
    }
}
