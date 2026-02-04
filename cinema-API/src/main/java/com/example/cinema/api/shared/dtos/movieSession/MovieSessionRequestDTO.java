package com.example.cinema.api.shared.dtos.movieSession;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieSessionRequestDTO {

    @NotNull(message = "A data da sessão não pode ser nula")
    @FutureOrPresent(message = "A data da sessão não pode estar no passado")
    private LocalDate showDate;

    @NotNull(message = "A hora inicial da sessão não pode ser nula")
    private LocalTime startTime;

    @NotNull(message = "A hora final da sessão não pode ser nula")
    private LocalTime endTime;

    @NotNull(message = "O valor não pode ser nulo")
    @Positive(message = "O valor deve ser maior que zero")
    private BigDecimal basePrice;

    private Long roomId;

    private Long movieId;
}

