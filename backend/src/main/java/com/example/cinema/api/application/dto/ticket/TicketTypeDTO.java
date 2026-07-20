package com.example.cinema.api.application.dto.ticket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketTypeDTO {
    private Long id;
    private String name;
    private String description;
    private String category;
    private BigDecimal price;
    private BigDecimal originalPrice;
}
