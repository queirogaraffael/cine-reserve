package com.example.cinema.api;

import com.example.cinema.api.domain.exception.DomainException;

public abstract class ResourceNotFoundException extends DomainException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}