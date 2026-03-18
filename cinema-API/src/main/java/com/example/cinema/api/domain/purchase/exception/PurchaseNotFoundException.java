package com.example.cinema.api.domain.purchase.exception;

import com.example.cinema.api.domain.exception.ResourceNotFoundException;

public class PurchaseNotFoundException extends ResourceNotFoundException {
    public PurchaseNotFoundException(String message) {
        super(message);
    }
}
