package com.example.cinema.api.application.dto.user;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CinemaAdminResponseDTO {
    private UUID id;
    private String name;
    private String email;
    private String phone;
}
