package com.example.cinema.api.application.dto.purchase;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseResponseDTO {

    private Long id;
    private LocalDateTime purchaseDate;
    private BigDecimal totalPrice;

    private UUID userId;
    private Long ticketId;
    private Long movieSessionId;
}
