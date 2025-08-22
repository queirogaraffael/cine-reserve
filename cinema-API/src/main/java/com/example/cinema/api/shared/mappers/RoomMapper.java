package com.example.cinema.api.shared.mappers;

import com.example.cinema.api.domain.entities.Room;
import com.example.cinema.api.shared.dtos.room.RoomRequestDTO;
import com.example.cinema.api.shared.dtos.room.RoomResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoomMapper {

    Room toEntity(RoomRequestDTO roomRequestDTO);

    RoomResponseDTO toDTO(Room room);

    void updateEntityFromDTO(RoomRequestDTO dto, @MappingTarget Room room);

}
