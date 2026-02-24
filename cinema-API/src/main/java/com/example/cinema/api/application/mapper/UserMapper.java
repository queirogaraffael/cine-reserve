package com.example.cinema.api.application.mapper;

import com.example.cinema.api.domain.user.User;
import com.example.cinema.api.application.dto.user.UserCreatedResponseDTO;
import com.example.cinema.api.application.dto.user.UserResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserCreatedResponseDTO toResponseDTO(User user);

    UserResponseDTO toUserResponseDTO(User user);
}
