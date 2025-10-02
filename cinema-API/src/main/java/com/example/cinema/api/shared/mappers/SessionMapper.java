package com.example.cinema.api.shared.mappers;

import com.example.cinema.api.domain.entities.MovieSession;
import com.example.cinema.api.shared.dtos.movieSession.MovieSessionRequestDTO;
import com.example.cinema.api.shared.dtos.movieSession.MovieSessionResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SessionMapper {

    MovieSession toEntity(MovieSessionRequestDTO movieSessionRequestDTO);

    @Mapping(target = "movieId", source = "movie.id")
    @Mapping(target = "roomId", source = "cinemaRoom.id")
    MovieSessionResponseDTO toResponseDTO(MovieSession movieSession);

}
