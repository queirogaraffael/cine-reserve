package com.example.cinema.api.domain.room.exception;

import com.example.cinema.api.shared.exception.ConflictException;

public class RoomNumberAlreadyExistsException extends ConflictException {
    public RoomNumberAlreadyExistsException(String message) {
        super(message);
    }
}
