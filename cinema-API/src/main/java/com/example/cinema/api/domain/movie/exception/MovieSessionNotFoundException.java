package com.example.cinema.api.domain.movie.exception;

import com.example.cinema.api.domain.exception.ResourceNotFoundException;

public class MovieSessionNotFoundException extends ResourceNotFoundException {
    public MovieSessionNotFoundException(String message) {
        super(message);
    }
}
