package com.example.cinema.api.domain.room.exception;

import com.example.cinema.api.domain.exception.ValidationException;

public class RoomInvalidNumberException extends ValidationException {
    public RoomInvalidNumberException(String message) {
        super(message);
    }
}
