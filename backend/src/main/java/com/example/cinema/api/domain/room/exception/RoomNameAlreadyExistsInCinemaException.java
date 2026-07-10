package com.example.cinema.api.domain.room.exception;

import com.example.cinema.api.domain.exception.StateConflictException;

public class RoomNameAlreadyExistsInCinemaException extends StateConflictException {
    public RoomNameAlreadyExistsInCinemaException(String message) {
        super(message);
    }
}
