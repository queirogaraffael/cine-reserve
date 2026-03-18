package com.example.cinema.api.domain.movie.exception;

import com.example.cinema.api.domain.exception.ResourceNotFoundException;

public class MovieNotFoundException extends ResourceNotFoundException {
    public MovieNotFoundException(String message) {
        super(message);
    }
}
