package com.example.cinema.api.domain.registration.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class EmailVerificationEvent {
    private UUID userId;
    private String email;
    private String name;
    private String code;
}