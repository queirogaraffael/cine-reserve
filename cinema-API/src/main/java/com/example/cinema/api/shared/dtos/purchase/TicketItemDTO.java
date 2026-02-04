package com.example.cinema.api.shared.dtos.purchase;


import com.example.cinema.api.domain.enums.TicketCategory;
import lombok.Data;

@Data
public class TicketItemDTO {

    private Long reservationId;
    private TicketCategory ticketCategory;
}
