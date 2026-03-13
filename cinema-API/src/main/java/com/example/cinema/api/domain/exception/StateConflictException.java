package com.example.cinema.api.domain.exception;

public class StateConflictException extends DomainException {
    public StateConflictException(String message) {
        super(message);
    }
}