package com.example.cinema.api.domain.room.exception;

public class RoomNumberAlreadyExistsException extends RuntimeException{
    public RoomNumberAlreadyExistsException() {
    }

    public RoomNumberAlreadyExistsException(String message) {
        super(message);
    }
}
