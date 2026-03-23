package com.example.cinema.api.domain.exception;

public abstract class ResourceNotFoundException extends DomainException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}