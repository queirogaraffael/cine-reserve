package com.example.cinema.api.domain.genre.exception;

public class GenreAlreadyExistsException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public GenreAlreadyExistsException(String message) {
        super(message);
    }
}
