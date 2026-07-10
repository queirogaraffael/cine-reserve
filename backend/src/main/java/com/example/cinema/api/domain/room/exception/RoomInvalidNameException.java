package com.example.cinema.api.domain.room.exception;

import com.example.cinema.api.domain.exception.ValidationException;

public class RoomInvalidNameException extends ValidationException {
    public RoomInvalidNameException(String message) {
        super(message);
    }
}
