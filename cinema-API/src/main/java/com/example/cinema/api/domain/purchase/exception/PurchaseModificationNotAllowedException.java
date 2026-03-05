package com.example.cinema.api.domain.purchase.exception;

public class PurchaseModificationNotAllowedException extends RuntimeException {
    public PurchaseModificationNotAllowedException(String message) {
        super(message);
    }
}
