package com.example.cinema.api.shared.fixtures;

import com.example.cinema.api.domain.user.User;
import com.example.cinema.api.domain.user.UserRole;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class UserFixture {

    public static User valid() {
        return new User(
                "raffael.queiroga",
                "529.982.247-25",
                "Raffael Queiroga",
                "raffael.queiroga@example.com",
                "hashed_password",
                LocalDate.of(1998, 1, 1),
                LocalDate.of(2024, 1, 1),
                UserRole.USER
        );
    }

    public static User admin() {
        return new User(
                "admin.user",
                "123.456.789-09",
                "Admin User",
                "admin@example.com",
                "hashed_password",
                LocalDate.of(1990, 1, 1),
                LocalDate.of(2024, 1, 1),
                UserRole.ADMIN
        );
    }

    public static User locked() {
        User user = valid();
        user.lockAccount(LocalDateTime.now().plusMinutes(30));
        return user;
    }
}