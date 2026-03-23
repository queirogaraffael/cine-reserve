package com.example.cinema.api.domain.genre.exception;

import com.example.cinema.api.domain.exception.ResourceNotFoundException;

public class GenreNotFoundException extends ResourceNotFoundException {
    public GenreNotFoundException(String message) {
        super(message);
    }
}
