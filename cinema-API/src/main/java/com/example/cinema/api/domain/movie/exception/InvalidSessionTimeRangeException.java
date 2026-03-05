package com.example.cinema.api.domain.movie.exception;

public class InvalidSessionTimeRangeException extends RuntimeException {
    public InvalidSessionTimeRangeException(String message) {
        super(message);
    }
}
