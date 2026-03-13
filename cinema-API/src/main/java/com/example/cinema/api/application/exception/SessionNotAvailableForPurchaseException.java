package com.example.cinema.api.application.exception;

public class SessionNotAvailableForPurchaseException extends RuntimeException {
    public SessionNotAvailableForPurchaseException(String message) {
        super(message);
    }
}
