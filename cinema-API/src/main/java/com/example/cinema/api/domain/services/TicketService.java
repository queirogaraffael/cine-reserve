package com.example.cinema.api.domain.services;

import com.example.cinema.api.domain.entities.MovieSession;
import com.example.cinema.api.domain.entities.Ticket;
import com.example.cinema.api.domain.entities.User;
import com.example.cinema.api.infrastructure.persistence.MovieSessionRepositoryJpa;
import com.example.cinema.api.infrastructure.persistence.TicketRepositoryJpa;
import com.example.cinema.api.shared.dtos.tickets.TicketRequestDTO;
import com.example.cinema.api.shared.dtos.tickets.TicketResponseDTO;
import com.example.cinema.api.shared.exceptions.ResourceNotFoundException;
import com.example.cinema.api.shared.mappers.TicketMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TicketService {

    private final TicketRepositoryJpa ticketRepositoryJpa;
    private final MovieSessionRepositoryJpa movieSessionRepositoryJpa;
    private final TicketMapper ticketMapper;
    private final UserService userService;

    public TicketService(TicketRepositoryJpa ticketRepositoryJpa, MovieSessionRepositoryJpa movieSessionRepositoryJpa, TicketMapper ticketMapper, UserService userService) {
        this.ticketRepositoryJpa = ticketRepositoryJpa;
        this.movieSessionRepositoryJpa = movieSessionRepositoryJpa;
        this.ticketMapper = ticketMapper;
        this.userService = userService;
    }

    @Transactional
    public TicketResponseDTO criarTickt(TicketRequestDTO ticketRequestDTO) {

        MovieSession movieSession = movieSessionRepositoryJpa.findById(ticketRequestDTO.getMovieSessionId())
                .orElseThrow(() -> new ResourceNotFoundException("Sessão de filme não encontrada"));

        Integer roomCapacity = movieSessionRepositoryJpa.findRoomCapacityByMovieSessionId(ticketRequestDTO.getMovieSessionId());

        if (ticketRequestDTO.getSeatNumber() > roomCapacity) {
            throw new IllegalArgumentException("Assento inválido");
        }

        boolean isSeatTaken = ticketRepositoryJpa.isSeatTaken(ticketRequestDTO.getSeatNumber(), ticketRequestDTO.getMovieSessionId());

        if (isSeatTaken) {
            throw new IllegalArgumentException("Assento já reservado");
        }

        Ticket ticket = ticketMapper.toEntity(ticketRequestDTO);

        ticket.setMovieSession(movieSession);

        User user = userService.getAuthenticatedUser();

        ticket.setUser(user);

        Ticket savedTicket = ticketRepositoryJpa.save(ticket);

        return ticketMapper.toResponseDTO(savedTicket);
    }
}
