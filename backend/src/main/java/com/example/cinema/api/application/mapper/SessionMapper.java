package com.example.cinema.api.application.mapper;

import com.example.cinema.api.domain.movie.MovieSession;
import com.example.cinema.api.application.dto.movieSession.MovieSessionRequestDTO;
import com.example.cinema.api.application.dto.movieSession.MovieSessionResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SessionMapper {

    MovieSession toEntity(MovieSessionRequestDTO movieSessionRequestDTO);

    @Mapping(target = "exhibitionId", source = "movieExhibition.id")
    @Mapping(target = "roomId", source = "cinemaRoom.id")
    MovieSessionResponseDTO toResponseDTO(MovieSession movieSession);

}
