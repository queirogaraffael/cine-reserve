package com.example.cinema.api.application.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class OrderResponseDTO {

    private Long id;
    private String status;
    private Long sessionId;
    private BigDecimal totalPrice;
    private BigDecimal serviceFee;
    private Integer totalTicketsCount;
    private LocalDateTime createdAt;
    private LocalDateTime reservationExpiresAt;
    private List<OrderItemResponseDTO> items;
}
