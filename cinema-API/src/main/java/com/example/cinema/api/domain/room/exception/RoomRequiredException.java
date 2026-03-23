package com.example.cinema.api.domain.room.exception;

import com.example.cinema.api.domain.exception.ValidationException;

public class RoomRequiredException extends ValidationException {
    public RoomRequiredException(String message) {
        super(message);
    }
}
