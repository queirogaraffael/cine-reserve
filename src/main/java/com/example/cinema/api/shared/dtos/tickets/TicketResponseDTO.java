package com.example.cinema.api.shared.dtos.tickets;

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

    private UUID userId;
    private Long movieSessionId;
    private Long purchaseId;
}
