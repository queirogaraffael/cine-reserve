package com.example.cinema.api.application.dto.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderItemRequestDTO {

    @NotNull
    private Long ticketTypeId;

    @NotNull
    @Min(1)
    private Integer quantity;
}
