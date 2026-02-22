package com.example.cinema.api.shared.dtos.login;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSessionDTO {

    private UUID userId;

    private String deviceId;

    private String userAgent;

    private String ip;

    private Instant createdAt;
}