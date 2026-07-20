package com.example.cinema.api.application.dto.email;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmailVerificationNotificationData {
    private String name;
    private String email;
    private String code;
}