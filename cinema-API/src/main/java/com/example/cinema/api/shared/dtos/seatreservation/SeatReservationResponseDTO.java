package com.example.cinema.api.shared.dtos.seatreservation;

import com.example.cinema.api.domain.seatreservation.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatReservationResponseDTO {

    private Long id;

    private Long movieSessionId;

    private Integer seatNumber;

    private ReservationStatus status;

    private LocalDateTime expiresAt;
}
