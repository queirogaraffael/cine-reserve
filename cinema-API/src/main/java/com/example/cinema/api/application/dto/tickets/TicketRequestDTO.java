package com.example.cinema.api.application.dto.tickets;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketRequestDTO {

    private int seatNumber;

    private Long movieSessionId;
}
