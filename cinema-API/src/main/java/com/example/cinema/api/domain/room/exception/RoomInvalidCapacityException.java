package com.example.cinema.api.domain.room.exception;

import com.example.cinema.api.domain.exception.ValidationException;

public class RoomInvalidCapacityException extends ValidationException {
    public RoomInvalidCapacityException(String message) {
        super(message);
    }
}
