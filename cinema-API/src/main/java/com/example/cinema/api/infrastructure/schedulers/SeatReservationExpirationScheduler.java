package com.example.cinema.api.infrastructure.schedulers;

import com.example.cinema.api.infrastructure.persistence.SeatReservationRepositoryJpa;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class SeatReservationExpirationScheduler {

    private final SeatReservationRepositoryJpa reservationRepository;

    @Scheduled(fixedRateString = "${seat.reservation.expiration.fixed-rate-ms}")
    @Transactional
    public void expireOldReservations() {
        reservationRepository.expireOldReservations(LocalDateTime.now());
    }
}

