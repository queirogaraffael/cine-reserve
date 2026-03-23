package com.example.cinema.api.application.dto.email;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PurchaseCreatedNotificationData {

    private final String name;
    private final String email;
    private final LocalDateTime purchaseDate;
    private final BigDecimal totalPrice;
}
