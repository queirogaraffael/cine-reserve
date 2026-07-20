package com.example.cinema.api.application.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class CreateOrderRequestDTO {

    @NotNull
    private Long sessionId;

    @NotEmpty
    @Valid
    private List<OrderItemRequestDTO> items;
}
