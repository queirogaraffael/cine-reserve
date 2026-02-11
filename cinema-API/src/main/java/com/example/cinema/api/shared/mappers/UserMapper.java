package com.example.cinema.api.shared.mappers;

import com.example.cinema.api.domain.user.User;
import com.example.cinema.api.shared.dtos.user.UserCreatedResponseDTO;
import com.example.cinema.api.shared.dtos.user.UserResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserCreatedResponseDTO toResponseDTO(User user);

    UserResponseDTO toUserResponseDTO(User user);
}
