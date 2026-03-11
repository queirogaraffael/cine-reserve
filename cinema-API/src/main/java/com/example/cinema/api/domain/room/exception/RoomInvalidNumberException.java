package com.example.cinema.api.domain.room.exception;

import com.example.cinema.api.shared.exception.BadRequestException;

public class RoomInvalidNumberException extends BadRequestException {
    public RoomInvalidNumberException(String message) {
        super(message);
    }
}
