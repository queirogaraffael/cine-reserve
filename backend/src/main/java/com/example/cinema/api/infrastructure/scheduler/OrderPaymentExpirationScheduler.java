package com.example.cinema.api.infrastructure.scheduler;

import com.example.cinema.api.infrastructure.persistence.OrderPaymentRepositoryJpa;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
        int updated = paymentRepository.expirePendingPaymentsOlderThan5Minutes();
        if (updated > 0) {
            log.info("Expirados {} pagamentos que ficaram travados em PENDING", updated);
        }
    }
}
