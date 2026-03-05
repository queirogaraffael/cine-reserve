package com.example.cinema.api.domain.movie.exception;

public class MovieGenreRequiredException extends RuntimeException {
    public MovieGenreRequiredException(String message) {
        super(message);
    }
}
