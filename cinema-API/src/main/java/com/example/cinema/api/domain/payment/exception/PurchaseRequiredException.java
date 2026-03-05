package com.example.cinema.api.domain.payment.exception;

public class PurchaseRequiredException extends RuntimeException {
    public PurchaseRequiredException(String message) {
        super(message);
    }
}
