package com.example.cinema.api.infrastructure.scheduler;

import com.example.cinema.api.domain.order.OrderStatus;
import com.example.cinema.api.infrastructure.persistence.OrderRepositoryJpa;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@Slf4j
public class OrderCleanupScheduler {

    private final OrderRepositoryJpa orderRepositoryJpa;

    public OrderCleanupScheduler(OrderRepositoryJpa orderRepositoryJpa) {
        this.orderRepositoryJpa = orderRepositoryJpa;
    }

    @Scheduled(cron = "0 0 3 * * ?") // Todos os dias as 3 da manha
    @Transactional
    public void cleanupStaleOrders() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(24);
        int deletedCount = orderRepositoryJpa.deleteByStatusAndCreatedAtBefore(OrderStatus.CREATED, cutoff);
        if (deletedCount > 0) {
            log.info("Faxina de pedidos: {} pedidos CREATED orfaos foram removidos.", deletedCount);
        }
    }
}
