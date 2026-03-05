package com.example.cinema.api.application.mapper;


import com.example.cinema.api.domain.seatreservation.SeatReservation;
import com.example.cinema.api.application.dto.seatreservation.SeatReservationRequestDTO;
import com.example.cinema.api.application.dto.seatreservation.SeatReservationResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SeatReservationMapper {

    @Mapping(target = "movieSessionId", source = "movieSession.id")
    SeatReservationResponseDTO toResponseDTO(SeatReservation seatReservation);
}
