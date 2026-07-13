package com.example.cinema.api.application.dto.movieSession;

import com.example.cinema.api.application.dto.movie.MovieDetailsDTO;
import com.example.cinema.api.domain.movie.AudioType;
import com.example.cinema.api.domain.movie.MovieFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ExhibitionSessionsResponseDTO {

    private Long exhibitionId;
    private MovieFormat format;
    private AudioType audio;
    private MovieDetailsDTO movieDetails;
    private List<DaySessionsDTO> days;
}
