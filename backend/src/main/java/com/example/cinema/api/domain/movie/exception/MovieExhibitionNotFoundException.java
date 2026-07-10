package com.example.cinema.api.domain.movie.exception;

import com.example.cinema.api.domain.exception.ResourceNotFoundException;

public class MovieExhibitionNotFoundException extends ResourceNotFoundException {
    public MovieExhibitionNotFoundException(String message) {
        super(message);
    }
}
