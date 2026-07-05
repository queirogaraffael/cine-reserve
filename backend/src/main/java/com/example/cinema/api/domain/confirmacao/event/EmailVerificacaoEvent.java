package com.example.cinema.api.domain.confirmacao.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class EmailVerificacaoEvent {
    private UUID usuarioId;
    private String email;
    private String nome;
    private String codigo;
}
