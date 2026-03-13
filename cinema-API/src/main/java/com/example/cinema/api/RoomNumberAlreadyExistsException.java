package com.example.cinema.api;

import com.example.cinema.api.domain.exception.StateConflictException;

public class RoomNumberAlreadyExistsException extends StateConflictException {
    public RoomNumberAlreadyExistsException(String message) {
        super(message);
    }
}
