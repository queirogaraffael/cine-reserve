package com.example.cinema.api.domain.order.exception;

import com.example.cinema.api.domain.exception.DomainException;

public class OrderModificationNotAllowedException extends DomainException {
    public OrderModificationNotAllowedException(String message) {
        super(message);
    }
}
