package com.example.cinema.api.domain.ticket.exception;

import com.example.cinema.api.domain.exception.ResourceNotFoundException;

public class TicketNotFoundException extends ResourceNotFoundException {
    public TicketNotFoundException(String message) {
        super(message);
    }
}
