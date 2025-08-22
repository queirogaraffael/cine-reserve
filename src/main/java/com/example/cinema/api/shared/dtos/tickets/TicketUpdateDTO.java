package com.example.cinema.api.shared.dtos.tickets;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketUpdateDTO {
    private int seatNumber;

    private Long movieSessionId;
}
