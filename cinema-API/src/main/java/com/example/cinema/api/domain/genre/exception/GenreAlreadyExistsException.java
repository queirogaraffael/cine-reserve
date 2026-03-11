package com.example.cinema.api.domain.genre.exception;

import com.example.cinema.api.shared.exception.ConflictException;

public class GenreAlreadyExistsException extends ConflictException {

    public GenreAlreadyExistsException(String message) {
        super(message);
    }
}
