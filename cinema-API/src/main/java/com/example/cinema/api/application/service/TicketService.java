package com.example.cinema.api.application.service;

import com.example.cinema.api.domain.user.User;
import com.example.cinema.api.infrastructure.persistence.TicketRepositoryJpa;
import com.example.cinema.api.shared.dtos.tickets.TicketResponseDTO;
import com.example.cinema.api.shared.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TicketService {

    private final TicketRepositoryJpa ticketRepositoryJpa;
    private final UserService userService;

    public TicketService(TicketRepositoryJpa ticketRepositoryJpa, UserService userService) {
        this.ticketRepositoryJpa = ticketRepositoryJpa;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public TicketResponseDTO getById(Long idTicket){

        User user = userService.getAuthenticatedUser();

        return ticketRepositoryJpa.findDtoByIdAndUser(idTicket, user).orElseThrow(()-> new ResourceNotFoundException("Ticket: " + idTicket + " não encontrado."));
    }

    @Transactional(readOnly = true)
    public List<TicketResponseDTO> getAllByPurchaseId(Long idPurchase){
        User user = userService.getAuthenticatedUser();

        List<TicketResponseDTO> tickets = ticketRepositoryJpa.findAllDtosByPurchaseIdAndUser(idPurchase, user);

        if(tickets.isEmpty()){
            throw new ResourceNotFoundException("Tickets para a compra: " + idPurchase + " não encontrado(s).");
        }

        return tickets;
    }

}
