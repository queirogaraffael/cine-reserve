package com.example.cinema.api.application.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmEmailDTO {

    @NotBlank(message = "O código é obrigatório.")
    @Size(min = 6, max = 6, message = "O código deve ter 6 dígitos.")
    private String token;
}
