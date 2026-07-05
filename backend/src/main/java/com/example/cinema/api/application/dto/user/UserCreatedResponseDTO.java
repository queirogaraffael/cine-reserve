package com.example.cinema.api.application.dto.user;

import com.example.cinema.api.domain.user.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreatedResponseDTO {
    private UUID id;
    private String name;
    private String email;
    private UserRole role;
    private String accessToken;
    private String refreshToken;
}
