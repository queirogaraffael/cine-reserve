package com.example.cinema.api.application.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordData {

    @NotBlank(message = "A senha atual não pode ser vazia")
    @Size(min = 6, max = 100, message = "A senha atual deve ter entre 6 e 100 caracteres")
    private String currentPassword;

    @NotBlank(message = "A nova senha não pode ser vazia")
    @Size(min = 8, max = 100, message = "A nova senha deve ter entre 8 e 100 caracteres")
    private String newPassword;
}