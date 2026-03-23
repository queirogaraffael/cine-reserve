package com.example.cinema.api.domain.purchase.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class PurchaseCreatedEvent {

    private final Long purchaseId;
    private final UUID userId;
    private final BigDecimal totalPrice;
    private final LocalDateTime purchaseDate;

}