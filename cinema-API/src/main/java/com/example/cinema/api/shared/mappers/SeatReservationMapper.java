package com.example.cinema.api.shared.mappers;


import com.example.cinema.api.domain.entities.SeatReservation;
import com.example.cinema.api.shared.dtos.seatreservation.SeatReservationRequestDTO;
import com.example.cinema.api.shared.dtos.seatreservation.SeatReservationResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SeatReservationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "movieSession", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "expiresAt", ignore = true)
    SeatReservation toEntity(SeatReservationRequestDTO dto);

    @Mapping(target = "movieSessionId", source = "movieSession.id")
    SeatReservationResponseDTO toResponseDTO(SeatReservation seatReservation);
}
