package com.example.cinema.api.shared.exception;

public class ExternalServicePermanentException extends RuntimeException {
    public ExternalServicePermanentException(String message) {
        super(message);
    }
}
