package com.example.cinema.api.application.dto.movie;

import com.example.cinema.api.domain.movie.AudioType;
import com.example.cinema.api.domain.movie.MovieCategoryFilter;
import com.example.cinema.api.domain.movie.MovieFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovieListingFilterDTO {
    private MovieFormat format;
    private AudioType audio;
    private MovieCategoryFilter category;
}
