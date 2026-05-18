package com.example.cinema.api.domain.payment.exception;

import com.example.cinema.api.domain.exception.ValidationException;

public class PaymentValidationException extends ValidationException {
    public PaymentValidationException(String message) {
        super(message);
    }
}