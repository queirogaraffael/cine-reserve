package com.example.cinema.api.shared.mappers;

import com.example.cinema.api.domain.genre.Genre;
import com.example.cinema.api.infrastructure.persistence.projection.GenreResponseDTOProjection;
import com.example.cinema.api.shared.dtos.genre.GenreRequestDTO;
import com.example.cinema.api.shared.dtos.genre.GenreResponseDTO;
import com.example.cinema.api.shared.dtos.genre.GenreUpdateDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface GenreMapper {

    Genre toEntity(GenreRequestDTO dto);

    GenreResponseDTO toDTO(Genre genre);
    void updateEntityFromDTO(GenreUpdateDTO dto, @MappingTarget Genre genre);
    GenreResponseDTO toDTO(GenreResponseDTOProjection projection);

}
