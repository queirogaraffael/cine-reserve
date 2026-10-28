package com.example.cinema.api.domain.payment.exception;

import com.example.cinema.api.domain.exception.StateConflictException;

public class OrderAlreadyPaidException extends StateConflictException {
    public OrderAlreadyPaidException(String message) {
        super(message);
    }
}
