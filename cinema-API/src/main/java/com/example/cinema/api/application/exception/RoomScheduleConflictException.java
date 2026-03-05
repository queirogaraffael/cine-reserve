package com.example.cinema.api.application.exception;

public class RoomScheduleConflictException extends RuntimeException {
    public RoomScheduleConflictException(String message) {
        super(message);
    }
}
