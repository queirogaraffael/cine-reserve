package com.example.cinema.api.application.mapper;

import com.example.cinema.api.domain.room.Room;
import com.example.cinema.api.application.dto.room.RoomRequestDTO;
import com.example.cinema.api.application.dto.room.RoomResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoomMapper {

    @Mapping(source = "cinema.id", target = "cinemaId")
    RoomResponseDTO toDTO(Room room);

    void updateEntityFromDTO(RoomRequestDTO dto, @MappingTarget Room room);

}
