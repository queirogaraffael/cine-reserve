package com.example.cinema.api.application.dto.movieSession;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieSessionResponseDTO {
    private Long id;

    private LocalDate showDate;

    private LocalTime startTime;
    private LocalTime endTime;

    private BigDecimal basePrice;

    private boolean canceled;

    private Long roomId;
    private Long movieId;
}
