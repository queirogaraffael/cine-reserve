package com.example.cinema.api.shared.mappers;

import com.example.cinema.api.domain.entities.Ticket;
import com.example.cinema.api.shared.dtos.tickets.TicketRequestDTO;
import com.example.cinema.api.shared.dtos.tickets.TicketResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TicketMapper {

    Ticket toEntity(TicketRequestDTO ticketRequestDTO);

    TicketResponseDTO toResponseDTO(Ticket ticket);
}
