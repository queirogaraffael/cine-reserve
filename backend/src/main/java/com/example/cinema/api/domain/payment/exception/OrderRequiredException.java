package com.example.cinema.api.domain.payment.exception;

import com.example.cinema.api.domain.exception.ValidationException;

public class OrderRequiredException extends ValidationException {
    public OrderRequiredException(String message) {
        super(message);
    }
}
