package com.example.cinema.api.infrastructure.scheduler;

import com.example.cinema.api.infrastructure.persistence.OrderPaymentRepositoryJpa;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@Slf4j
public class OrderPaymentExpirationScheduler {

    private final OrderPaymentRepositoryJpa paymentRepository;

    public OrderPaymentExpirationScheduler(OrderPaymentRepositoryJpa paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Scheduled(fixedDelay = 5 * 60 * 1000)
    @Transactional
    public void expirePendingPayments() {
        LocalDateTime cutoff = java.time.LocalDateTime.now().minusMinutes(5);
        int updated = paymentRepository.expirePendingPaymentsOlderThan(cutoff);
        if (updated > 0) {
            log.info("Expirados {} pagamentos que ficaram travados em PENDING", updated);
        }
    }
}
