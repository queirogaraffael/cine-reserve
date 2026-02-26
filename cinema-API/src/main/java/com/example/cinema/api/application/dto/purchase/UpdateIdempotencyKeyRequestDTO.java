package com.example.cinema.api.application.dto.purchase;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateIdempotencyKeyRequestDTO {

    @NotBlank(message = "Idempotency key é obrigatória")
    private String idempotencyKey;

}