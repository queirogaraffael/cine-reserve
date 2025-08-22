package com.example.cinema.api.domain.user.factories;

import com.example.cinema.api.shared.dtos.user.UserRequestDTO;
import com.example.cinema.api.domain.entities.User;

public class UserFactory {

    public static User createFromDto(UserRequestDTO dto, String encryptedPassword) {
        return new User(
                null,
                dto.getUsername(),
                dto.getName(),
                dto.getEmail(),
                encryptedPassword,
                dto.getDataJoined(),
                dto.getBirthdate(),
                dto.getRole(),
                dto.getCategory(),
                null,
                null
        );
    }

}
