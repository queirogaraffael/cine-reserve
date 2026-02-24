package com.example.cinema.api.domain.purchase.exception;

public class PurchaseAlreadyHasPaymentException extends RuntimeException {
    public PurchaseAlreadyHasPaymentException(String message) {
        super(message);
    }
}
