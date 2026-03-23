package com.example.cinema.api.domain.payment.exception;

import com.example.cinema.api.domain.exception.ValidationException;

public class PaymentMethodRequiredException extends ValidationException {
    public PaymentMethodRequiredException(String message) {
        super(message);
    }
}
