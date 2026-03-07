package com.example.cinema.api.domain.exception;

public class MovieSessionRequiredException extends RuntimeException {
    public MovieSessionRequiredException(String message) {
        super(message);
    }
}
