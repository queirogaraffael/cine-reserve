package com.example.cinema.api.domain.movie.exception;

public class MovieTitleRequiredException extends RuntimeException {
    public MovieTitleRequiredException(String message) {
        super(message);
    }
}
