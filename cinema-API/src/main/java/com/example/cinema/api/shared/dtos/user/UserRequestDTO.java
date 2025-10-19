package com.example.cinema.api.shared.dtos.user;

import com.example.cinema.api.domain.enums.UserCategory;
import com.example.cinema.api.domain.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDTO {

    @NotBlank(message = "O username é obrigatório.")
    @Size(min = 4, max = 50, message = "O username deve ter entre 4 e 50 caracteres.")
    private String username;

    @NotBlank(message = "O nome completo é obrigatório.")
    @Size(max = 100, message = "O nome não pode exceder 100 caracteres.")
    private String name;

    @NotBlank(message = "O e-mail é obrigatório e não pode ser vazio.")
    @Email(message = "O formato do e-mail é inválido.")
    @Size(max = 255, message = "O e-mail não pode exceder 255 caracteres.")
    private String email;

    @NotBlank(message = "A senha é obrigatória.")
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres.")
    private String password;

    @NotNull(message = "A data de cadastro é obrigatória.")
    private LocalDate dataJoined;

    @NotNull(message = "A data de nascimento é obrigatória.")
    @Past(message = "A data de nascimento deve ser uma data no passado.")
    private LocalDate birthdate;

    @NotNull(message = "A categoria do usuário é obrigatória.")
    private UserCategory category;

    @NotBlank(message = "O CPF não pode estar vazio")
    @CPF(message = "CPF inválido")
    private String cpf;

    private UserAddressDTO address;
}