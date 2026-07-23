package com.example.cinema.api.application.dto.user;

import com.example.cinema.api.domain.user.Address;
import com.example.cinema.api.domain.user.Gender;
import com.example.cinema.api.domain.user.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserContextDTO {
    private UUID id;
    private String name;
    private String email;
    private String phone;
    private Gender gender;
    private boolean emailConfirmed;
    private boolean active;
    private Address address;
    private UserRole role;
}
