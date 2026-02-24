package com.example.cinema.api.application.mapper;

import com.example.cinema.api.domain.ticket.Ticket;
import com.example.cinema.api.application.dto.tickets.TicketRequestDTO;
import com.example.cinema.api.application.dto.tickets.TicketResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TicketMapper {

    Ticket toEntity(TicketRequestDTO ticketRequestDTO);

    TicketResponseDTO toResponseDTO(Ticket ticket);
}
