package com.example.cinema.api.domain.payment.exception;

public class PaymentMethodRequiredException extends RuntimeException {
    public PaymentMethodRequiredException(String message) {
        super(message);
    }
}
