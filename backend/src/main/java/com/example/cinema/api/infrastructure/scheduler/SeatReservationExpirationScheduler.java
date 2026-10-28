package com.example.cinema.api.infrastructure.scheduler;

import com.example.cinema.api.infrastructure.persistence.OrderRepositoryJpa;
import com.example.cinema.api.infrastructure.persistence.SeatReservationRepositoryJpa;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class SeatReservationExpirationScheduler {

    private final SeatReservationRepositoryJpa reservationRepository;
    private final OrderRepositoryJpa orderRepository;

    @Scheduled(fixedRateString = "${seat.reservation.expiration.fixed-rate-ms}")
    @Transactional
    public void expireOldReservations() {
        int canceledOrders = orderRepository.cancelExpiredWaitingOrders(LocalDateTime.now());
        if (canceledOrders > 0) {
            log.info("Cancelou {} pedidos expirados (status WAITING_PAYMENT).", canceledOrders);
        }
        reservationRepository.expireOldReservations(LocalDateTime.now());
    }
}
