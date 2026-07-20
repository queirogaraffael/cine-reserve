package com.example.cinema.api.domain.ticket.exception;

import com.example.cinema.api.domain.exception.ResourceNotFoundException;

public class TicketTypeNotFoundException extends ResourceNotFoundException {
    public TicketTypeNotFoundException(String message) {
        super(message);
    }
}
