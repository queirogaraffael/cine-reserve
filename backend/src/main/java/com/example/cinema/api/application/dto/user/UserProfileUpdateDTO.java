package com.example.cinema.api.application.dto.user;

import com.example.cinema.api.domain.user.Gender;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileUpdateDTO {

    private Gender gender;

    @Past(message = "A data de nascimento deve ser no passado.")
    private LocalDate birthdate;

    @CPF(message = "CPF inválido.")
    private String cpf;
}
