package com.example.cinema.api.domain.user.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class UserCreatedEvent {
    private UUID userId;
    private String name;
    private String email;
    private String codigoVerificacao;
}
