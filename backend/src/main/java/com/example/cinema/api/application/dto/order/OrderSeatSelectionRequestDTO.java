package com.example.cinema.api.application.dto.order;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@NoArgsConstructor
public class OrderSeatSelectionRequestDTO {

    @NotEmpty
    private Set<Long> seatIds;
}
