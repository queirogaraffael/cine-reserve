package com.example.cinema.api.domain.payment.exception;

import com.example.cinema.api.domain.exception.ResourceNotFoundException;

public class PaymentNotFoundException extends ResourceNotFoundException {
    public PaymentNotFoundException(String message) {
        super(message);
    }
}
