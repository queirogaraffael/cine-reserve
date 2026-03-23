package com.example.cinema.api.application.mapper;

import com.example.cinema.api.domain.genre.Genre;
import com.example.cinema.api.infrastructure.persistence.projection.GenreResponseDTOProjection;
import com.example.cinema.api.application.dto.genre.GenreRequestDTO;
import com.example.cinema.api.application.dto.genre.GenreResponseDTO;
import com.example.cinema.api.application.dto.genre.GenreUpdateDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface GenreMapper {
    GenreResponseDTO toDTO(Genre genre);
    void updateEntityFromDTO(GenreUpdateDTO dto, @MappingTarget Genre genre);
    GenreResponseDTO toDTO(GenreResponseDTOProjection projection);

}
