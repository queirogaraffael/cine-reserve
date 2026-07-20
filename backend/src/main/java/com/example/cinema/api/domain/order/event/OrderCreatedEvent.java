package com.example.cinema.api.domain.order.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class OrderCreatedEvent {

    private final Long orderId;
    private final UUID userId;
    private final BigDecimal totalPrice;
    private final LocalDateTime createdAt;
}
