package com.example.cinema.api.application.dto.email;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserCreatedNotificationData {

    private String name;
    private String email;
}
