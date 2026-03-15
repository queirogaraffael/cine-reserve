package com.example.cinema.api.domain.room.exception;

import com.example.cinema.api.domain.exception.StateConflictException;

public class RoomScheduleConflictException extends StateConflictException {
    public RoomScheduleConflictException(String message) {
        super(message);
    }
}
