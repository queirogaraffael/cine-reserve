package com.example.cinema.api.shared.mappers;

import com.example.cinema.api.domain.entities.Movie;
import com.example.cinema.api.infrastructure.repositories.projection.MovieResponseDTOProjection;
import com.example.cinema.api.shared.dtos.movie.MovieRequestDTO;
import com.example.cinema.api.shared.dtos.movie.MovieResponseDTO;
import com.example.cinema.api.shared.dtos.movie.MovieUpdateDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MovieMapper {

    Movie toEntity(MovieRequestDTO dto);

    MovieResponseDTO toDTO(Movie movie);

    MovieResponseDTO projectionToDTO(MovieResponseDTOProjection movie);

    void updateEntityFromDTO(MovieUpdateDTO dto, @MappingTarget Movie movie);

}

