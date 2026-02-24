package com.example.cinema.api.application.dto.purchase;


import com.example.cinema.api.domain.ticket.TicketCategory;
import lombok.Data;

@Data
public class TicketItemDTO {

    private Long reservationId;
    private TicketCategory ticketCategory;
}
