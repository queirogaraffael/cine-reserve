package com.example.cinema.api.domain.movie.exception;

import com.example.cinema.api.domain.exception.ValidationException;

public class RoomCinemaMismatchException extends ValidationException {
    public RoomCinemaMismatchException(String message) {
        super(message);
    }
}
