package com.example.cinema.api.application.service;

import com.example.cinema.api.domain.ticket.exception.TicketNotFoundException;
import com.example.cinema.api.infrastructure.persistence.TicketRepositoryJpa;
import com.example.cinema.api.application.dto.tickets.TicketResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class TicketService {

    private final TicketRepositoryJpa ticketRepositoryJpa;

    public TicketService(TicketRepositoryJpa ticketRepositoryJpa) {
        this.ticketRepositoryJpa = ticketRepositoryJpa;
    }

    @Transactional(readOnly = true)
    public TicketResponseDTO getById(Long idTicket, UUID userId) {
        return ticketRepositoryJpa.findDtoByIdAndUserId(idTicket, userId)
                .orElseThrow(() -> new TicketNotFoundException("Ticket: " + idTicket + " não encontrado."));
    }

    @Transactional(readOnly = true)
    public List<TicketResponseDTO> getAllByPurchaseId(Long idPurchase, UUID userId) {
        List<TicketResponseDTO> tickets = ticketRepositoryJpa.findAllDtosByPurchaseIdAndUserId(idPurchase, userId);

        if (tickets.isEmpty()) {
            throw new TicketNotFoundException("Tickets para a compra: " + idPurchase + " não encontrado(s).");
        }

        return tickets;
    }

}
