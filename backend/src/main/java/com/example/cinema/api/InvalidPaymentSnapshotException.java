package com.example.cinema.api;

public class InvalidPaymentSnapshotException extends RuntimeException {
    public InvalidPaymentSnapshotException(String message) {
        super(message);
    }
}
