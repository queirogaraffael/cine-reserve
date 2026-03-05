package com.example.cinema.api.domain.room.exception;

public class RoomInvalidCapacityException extends RuntimeException {
    public RoomInvalidCapacityException(String message) {
        super(message);
    }
}
