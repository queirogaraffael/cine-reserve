package com.example.cinema.api.domain.cinema.exception;

import com.example.cinema.api.domain.exception.DomainException;

public class UserDoesNotBelongToCinemaException extends DomainException {
    public UserDoesNotBelongToCinemaException(String message) {
        super(message);
    }
}
