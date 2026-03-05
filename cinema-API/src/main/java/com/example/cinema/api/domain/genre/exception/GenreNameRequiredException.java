package com.example.cinema.api.domain.genre.exception;

public class GenreNameRequiredException extends RuntimeException {
    public GenreNameRequiredException(String message) {
        super(message);
    }
}
