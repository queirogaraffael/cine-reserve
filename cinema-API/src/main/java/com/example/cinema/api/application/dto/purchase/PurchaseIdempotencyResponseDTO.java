package com.example.cinema.api.application.dto.purchase;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseIdempotencyResponseDTO {

    private Long id;
    private String idempotencyKey;

}