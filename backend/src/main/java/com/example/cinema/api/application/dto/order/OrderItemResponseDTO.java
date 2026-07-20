package com.example.cinema.api.application.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
public class OrderItemResponseDTO {

    private Long id;
    private Long ticketTypeId;
    private String ticketTypeName;
    private String category;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
}
