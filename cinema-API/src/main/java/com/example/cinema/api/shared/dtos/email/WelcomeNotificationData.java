package com.example.cinema.api.shared.dtos.email;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WelcomeNotificationData {

    private String name;
    private String email;
}
