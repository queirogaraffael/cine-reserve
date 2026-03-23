package com.example.cinema.api.domain.genre.exception;

import com.example.cinema.api.domain.exception.StateConflictException;

public class GenreAlreadyExistsException extends StateConflictException {

    public GenreAlreadyExistsException(String message) {
        super(message);
    }
}
