package com.example.cinema.api.shared.fixtures;

import com.example.cinema.api.domain.user.Address;
import com.example.cinema.api.domain.user.Gender;
import com.example.cinema.api.domain.user.User;
import com.example.cinema.api.domain.user.UserRole;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class UserFixture {

    public static User valid() {
        return new User(
                "529.982.247-25",
                "Raffael Queiroga",
                "raffael.queiroga@example.com",
                "hashed_password",
                "11999999999",
                Gender.MALE,
                new Address("01001-000", "Praça da Sé", "100", "Apto 1", "Sé", "São Paulo", "SP"),
                true,
                LocalDate.of(2024, 1, 1),
                LocalDate.of(1998, 1, 1),
                UserRole.USER
        );
    }

    public static User admin() {
        return new User(
                "123.456.789-09",
                "Admin User",
                "admin@example.com",
                "hashed_password",
                "11988888888",
                Gender.MALE,
                new Address("01001-000", "Praça da Sé", "200", null, "Sé", "São Paulo", "SP"),
                true,
                LocalDate.of(2024, 1, 1),
                LocalDate.of(1990, 1, 1),
                UserRole.ADMIN
        );
    }

    public static User locked() {
        User user = valid();
        user.lockAccount(LocalDateTime.now().plusMinutes(30));
        return user;
    }

    public static User desativado() {
        User user = valid();
        user.deactivate();
        return user;
    }
}