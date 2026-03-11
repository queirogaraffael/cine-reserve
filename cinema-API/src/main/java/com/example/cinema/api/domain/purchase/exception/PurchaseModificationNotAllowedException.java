package com.example.cinema.api.domain.purchase.exception;

import com.example.cinema.api.shared.exception.ConflictException;

public class PurchaseModificationNotAllowedException extends ConflictException {
    public PurchaseModificationNotAllowedException(String message) {
        super(message);
    }
}
