package com.example.cinema.api.domain.movie.exception;

public class MovieDescriptionRequiredException extends RuntimeException {
    public MovieDescriptionRequiredException(String message) {
        super(message);
    }
}
