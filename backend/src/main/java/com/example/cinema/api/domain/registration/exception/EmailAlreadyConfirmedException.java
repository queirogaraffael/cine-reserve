package com.example.cinema.api.domain.registration.exception;

import com.example.cinema.api.domain.exception.DomainException;

public class EmailAlreadyConfirmedException extends DomainException {
    public EmailAlreadyConfirmedException(String message) {
        super(message);
    }
}