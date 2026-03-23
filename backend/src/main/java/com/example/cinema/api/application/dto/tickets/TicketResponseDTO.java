package com.example.cinema.api.application.dto.tickets;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketResponseDTO {

    private Long id;
    private int seatNumber;

    private Long movieSessionId;
    private Long purchaseId;
}
