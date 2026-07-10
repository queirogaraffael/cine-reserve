package com.example.cinema.api.application.dto.movie;

import com.example.cinema.api.domain.movie.MovieRating;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieResponseDTO {

    private Long id;
    private String title;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    private String imageUrl;
    private MovieRating rating;
    private boolean inTheaters;
    private boolean preRelease;

    public MovieResponseDTO(Long id, String title, String description, LocalDate releaseDate, int duration, String imageUrl) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.imageUrl = imageUrl;
    }
}
