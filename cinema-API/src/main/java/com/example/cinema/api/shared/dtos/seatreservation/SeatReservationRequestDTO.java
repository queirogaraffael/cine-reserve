package com.example.cinema.api.shared.dtos.seatreservation;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatReservationRequestDTO {

    @NotNull
    private Long movieSessionId;

    @NotNull
    @Min(1)
    private Integer seatNumber;
}
