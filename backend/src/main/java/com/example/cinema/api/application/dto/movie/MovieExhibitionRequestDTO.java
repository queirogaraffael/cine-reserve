package com.example.cinema.api.application.dto.movie;

import com.example.cinema.api.domain.movie.AudioType;
import com.example.cinema.api.domain.movie.MovieFormat;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovieExhibitionRequestDTO {

    @NotNull(message = "O id do filme é obrigatório.")
    private Long movieId;

    @NotNull(message = "O id do cinema é obrigatório.")
    private Long cinemaId;

    @NotNull(message = "O formato do filme é obrigatório.")
    private MovieFormat format;

    @NotNull(message = "O tipo de áudio é obrigatório.")
    private AudioType audio;
}
