package com.example.cinema.api.domain.genre.exception;

import com.example.cinema.api.domain.exception.ValidationException;

public class GenreNameRequiredException extends ValidationException {
    public GenreNameRequiredException(String message) {
        super(message);
    }
}
