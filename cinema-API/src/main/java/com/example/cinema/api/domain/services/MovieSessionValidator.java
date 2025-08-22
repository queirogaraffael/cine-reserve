package com.example.cinema.api.domain.services;

import com.example.cinema.api.shared.dtos.movieSession.MovieSessionRequestDTO;
import com.example.cinema.api.shared.dtos.movieSession.MovieSessionUpdateDTO;
import com.example.cinema.api.domain.enums.MovieSessionStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;

@Component
public class MovieSessionValidator {

    private final com.example.cinema.api.domain.repositories.MovieSessionRepository movieSessionRepository;
    private final Clock clock;

    public MovieSessionValidator(com.example.cinema.api.domain.repositories.MovieSessionRepository movieSessionRepository, Clock clock) {
        this.movieSessionRepository = movieSessionRepository;
        this.clock = clock;
    }

    public void validateSessionRequest(MovieSessionRequestDTO dto) {
        validateShowDate(dto.getShowDate());
        validateTimeRange(dto.getStartTime(), dto.getEndTime());
        validateBasePrice(dto.getBasePrice());
    }

    public void validateSessionUpdate(MovieSessionUpdateDTO dto) {
        validateShowDate(dto.getShowDate());
        validateTimeRange(dto.getStartTime(), dto.getEndTime());
        validateBasePrice(dto.getBasePrice());
    }

    public void validateSessionConflicts(MovieSessionRequestDTO dto) {
        boolean conflict = movieSessionRepository.existsSessionConflict(
                dto.getRoomId(),
                dto.getShowDate(),
                dto.getStartTime(),
                dto.getEndTime()
        );
        if (conflict) {
            throw new IllegalArgumentException("A sala já está reservada para esse horário.");
        }
    }

    public void validateSessionConflicts(MovieSessionUpdateDTO dto) {
        boolean conflict = movieSessionRepository.existsSessionConflict(
                dto.getRoomId(),
                dto.getShowDate(),
                dto.getStartTime(),
                dto.getEndTime()
        );
        if (conflict) {
            throw new IllegalArgumentException("A sala já está reservada para esse horário.");
        }
    }

    private void validateShowDate(LocalDate showDate) {
        if (showDate == null) {
            throw new IllegalArgumentException("A data da sessão (showDate) não pode ser nula.");
        }
        if (showDate.isBefore(LocalDate.now(clock))) {
            throw new IllegalArgumentException("A data da sessão não pode estar no passado.");
        }
    }

    private void validateTimeRange(LocalTime start, LocalTime end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Os horários de início e término não podem ser nulos.");
        }
        if (!start.isBefore(end)) {
            throw new IllegalArgumentException("A hora de início deve ser antes da hora de término.");
        }
    }

    private void validateBasePrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O preço do ingresso deve ser maior que zero.");
        }
    }

    private void validateStatus(MovieSessionStatus status) {
        if (status == MovieSessionStatus.CANCELED || status == MovieSessionStatus.FINISHED) {
            throw new IllegalArgumentException("O status da sessão não pode ser CANCELED ou FINISHED na criação.");
        }
    }
}
