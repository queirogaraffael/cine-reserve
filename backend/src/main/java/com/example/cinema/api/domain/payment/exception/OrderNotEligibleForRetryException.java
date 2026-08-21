package com.example.cinema.api.domain.payment.exception;

import com.example.cinema.api.domain.exception.ValidationException;

public class OrderNotEligibleForRetryException extends ValidationException {
    public OrderNotEligibleForRetryException(String message) {
        super(message);
    }
}
