package com.example.cinema.api.application.dto.user;

import com.example.cinema.api.domain.user.Endereco;
import com.example.cinema.api.domain.user.Sexo;
import com.example.cinema.api.domain.user.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserContextDTO {
    private UUID id;
    private String name;
    private String email;
    private String celular;
    private Sexo sexo;
    private boolean emailConfirmado;
    private Endereco endereco;
    private UserRole role;
}
