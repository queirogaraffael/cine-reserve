package com.example.cinema.api.domain.registration.exception;

import com.example.cinema.api.domain.exception.DomainException;

public class InvalidCodeException extends DomainException {
    public InvalidCodeException(String message) {
        super(message);
    }
}