package com.example.cinema.api.application.mapper;

import com.example.cinema.api.domain.movie.Movie;
import com.example.cinema.api.infrastructure.persistence.projection.MovieResponseDTOProjection;
import com.example.cinema.api.application.dto.movie.MovieRequestDTO;
import com.example.cinema.api.application.dto.movie.MovieResponseDTO;
import com.example.cinema.api.application.dto.movie.MovieUpdateDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MovieMapper {

    Movie toEntity(MovieRequestDTO dto);

    MovieResponseDTO toDTO(Movie movie);

    MovieResponseDTO projectionToDTO(MovieResponseDTOProjection movie);

    @Mapping(target = "inTheaters", ignore = true)
    @Mapping(target = "preRelease", ignore = true)
    void updateEntityFromDTO(MovieUpdateDTO dto, @MappingTarget Movie movie);

}
