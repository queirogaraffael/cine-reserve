package com.example.cinema.api.domain.movie.exception;

public class InvalidMovieDurationException extends RuntimeException {
    public InvalidMovieDurationException(String message) {
        super(message);
    }
}
