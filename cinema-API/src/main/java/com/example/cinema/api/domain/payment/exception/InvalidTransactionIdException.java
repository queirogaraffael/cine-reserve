package com.example.cinema.api.domain.payment.exception;

public class InvalidTransactionIdException extends RuntimeException {
    public InvalidTransactionIdException(String message) {
        super(message);
    }
}
