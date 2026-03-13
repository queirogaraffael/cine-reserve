package com.example.cinema.api.shared.exception;

public abstract class ResourceNotFound extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public ResourceNotFound(String message) {
        super(message);
    }

}
