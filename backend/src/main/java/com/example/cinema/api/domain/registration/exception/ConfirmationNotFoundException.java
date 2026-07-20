package com.example.cinema.api.domain.registration.exception;

import com.example.cinema.api.domain.exception.DomainException;

public class ConfirmationNotFoundException extends DomainException {
    public ConfirmationNotFoundException(String message) {
        super(message);
    }
}