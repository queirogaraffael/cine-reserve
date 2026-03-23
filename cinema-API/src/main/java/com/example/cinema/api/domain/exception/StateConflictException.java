package com.example.cinema.api.domain.exception;

public abstract class StateConflictException extends DomainException {
    public StateConflictException(String message) {
        super(message);
    }
}