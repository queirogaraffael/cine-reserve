package com.example.cinema.api.shared.dtos.user;

import com.example.cinema.api.domain.enums.UserCategory;
import com.example.cinema.api.domain.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDTO{
        private String username;
        private String name;
        private String email;
        private String password;
        private LocalDate dataJoined;
        private LocalDate birthdate;
        private UserRole role;
        private UserCategory category;
}
