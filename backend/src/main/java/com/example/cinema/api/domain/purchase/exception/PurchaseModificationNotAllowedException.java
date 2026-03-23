package com.example.cinema.api.domain.purchase.exception;

import com.example.cinema.api.domain.exception.StateConflictException;

public class PurchaseModificationNotAllowedException extends StateConflictException {
    public PurchaseModificationNotAllowedException(String message) {
        super(message);
    }
}
