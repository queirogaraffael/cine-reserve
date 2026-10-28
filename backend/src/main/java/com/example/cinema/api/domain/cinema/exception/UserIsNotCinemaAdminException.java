package com.example.cinema.api.domain.cinema.exception;

import com.example.cinema.api.domain.exception.DomainException;

public class UserIsNotCinemaAdminException extends DomainException {
    public UserIsNotCinemaAdminException(String message) {
        super(message);
    }
}
