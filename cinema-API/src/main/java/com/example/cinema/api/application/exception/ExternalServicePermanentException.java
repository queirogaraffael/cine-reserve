package com.example.cinema.api.application.exception;

public class ExternalServicePermanentException extends RuntimeException {
    public ExternalServicePermanentException(String message) {
        super(message);
    }
}
