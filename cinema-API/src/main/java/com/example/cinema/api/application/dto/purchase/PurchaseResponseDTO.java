package com.example.cinema.api.application.dto.purchase;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseResponseDTO {

    private Long id;
    private LocalDateTime purchaseDate;
    private BigDecimal totalPrice;

    private UUID userId;
    private String idempotencyKey;

    private List<Long> ticketIds;
}
