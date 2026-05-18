package com.example.cinema.api.application.exception;

public class InvalidPaymentSnapshotException extends RuntimeException {
    public InvalidPaymentSnapshotException(String message) {
        super(message);
    }
}
