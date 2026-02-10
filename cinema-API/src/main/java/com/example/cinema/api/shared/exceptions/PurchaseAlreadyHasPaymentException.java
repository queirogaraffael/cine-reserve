package com.example.cinema.api.shared.exceptions;

public class PurchaseAlreadyHasPaymentException extends RuntimeException {
    public PurchaseAlreadyHasPaymentException(String message) {
        super(message);
    }
}
