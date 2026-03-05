package com.example.cinema.api.domain.movie.exception;

public class ReleaseDateRequiredException extends RuntimeException {
    public ReleaseDateRequiredException(String message) {
        super(message);
    }
}
