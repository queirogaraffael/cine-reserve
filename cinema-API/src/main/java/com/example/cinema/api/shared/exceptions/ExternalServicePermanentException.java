package com.example.cinema.api.shared.exceptions;

public class ExternalServicePermanentException extends RuntimeException {
    public ExternalServicePermanentException(String message) {
        super(message);
    }
}
