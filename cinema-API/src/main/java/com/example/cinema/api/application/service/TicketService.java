package com.example.cinema.api.application.service;

import com.example.cinema.api.infrastructure.persistence.TicketRepositoryJpa;
import com.example.cinema.api.shared.mappers.TicketMapper;
import org.springframework.stereotype.Service;

@Service
public class TicketService {

    private final TicketRepositoryJpa ticketRepositoryJpa;
    private final TicketMapper ticketMapper;
    private final UserService userService;

    public TicketService(TicketRepositoryJpa ticketRepositoryJpa, TicketMapper ticketMapper, UserService userService) {
        this.ticketRepositoryJpa = ticketRepositoryJpa;
        this.ticketMapper = ticketMapper;
        this.userService = userService;
    }
}
