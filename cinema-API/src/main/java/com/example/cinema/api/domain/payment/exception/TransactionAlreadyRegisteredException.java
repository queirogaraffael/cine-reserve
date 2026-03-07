package com.example.cinema.api.domain.payment.exception;

public class TransactionAlreadyRegisteredException extends RuntimeException {
    public TransactionAlreadyRegisteredException(String message) {
        super(message);
    }
}
