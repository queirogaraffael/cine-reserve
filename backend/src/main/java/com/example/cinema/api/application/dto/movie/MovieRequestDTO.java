package com.example.cinema.api.application.dto.movie;

import com.example.cinema.api.domain.movie.MovieRating;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieRequestDTO {

    private String title;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    private String imageUrl;
    private MovieRating rating;
    private boolean preRelease;
    private Long genreId;

}
