package com.example.cinema.api.domain.order.exception;

import com.example.cinema.api.domain.exception.ResourceNotFoundException;

public class OrderNotFoundException extends ResourceNotFoundException {
    public OrderNotFoundException(String message) {
        super(message);
    }
}
