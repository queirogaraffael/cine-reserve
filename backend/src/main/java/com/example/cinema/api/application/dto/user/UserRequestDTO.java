package com.example.cinema.api.application.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDTO {

    @NotBlank(message = "O nome completo é obrigatório.")
    @Size(max = 100, message = "O nome não pode exceder 100 caracteres.")
    private String name;

    @NotBlank(message = "O e-mail é obrigatório e não pode ser vazio.")
    @Email(message = "O formato do e-mail é inválido.")
    @Size(max = 255, message = "O e-mail não pode exceder 255 caracteres.")
    private String email;

    @NotBlank(message = "A senha é obrigatória.")
    @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres.")
    private String password;

    @NotBlank(message = "O celular é obrigatório.")
    private String celular;
}