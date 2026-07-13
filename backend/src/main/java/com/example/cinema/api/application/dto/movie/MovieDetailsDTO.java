package com.example.cinema.api.application.dto.movie;

import com.example.cinema.api.domain.movie.Movie;
import com.example.cinema.api.domain.movie.MovieRating;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MovieDetailsDTO {

    private String title;
    private String description;
    private String posterUrl;
    private int duration;
    private MovieRating rating;
    private String genre;

    public MovieDetailsDTO(Movie movie) {
        this.title = movie.getTitle();
        this.description = movie.getDescription();
        this.posterUrl = movie.getImageUrl();
        this.duration = movie.getDuration();
        this.rating = movie.getRating();
        this.genre = movie.getGenre().getName();
    }
}
