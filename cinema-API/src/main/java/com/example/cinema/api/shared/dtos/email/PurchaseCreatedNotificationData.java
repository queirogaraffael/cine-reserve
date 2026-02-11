package com.example.cinema.api.shared.dtos.email;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PurchaseCreatedNotificationData {

    private Long IdPurchase;
    private String name;
    private LocalDateTime purchaseDate;
    private BigDecimal totalPrice;
    private String email;
}
