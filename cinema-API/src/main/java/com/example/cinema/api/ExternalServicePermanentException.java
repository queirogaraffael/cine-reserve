package com.example.cinema.api;

public class ExternalServicePermanentException extends RuntimeException {
    public ExternalServicePermanentException(String message) {
        super(message);
    }
}
