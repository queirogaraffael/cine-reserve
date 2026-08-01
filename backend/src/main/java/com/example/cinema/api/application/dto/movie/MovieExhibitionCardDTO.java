package com.example.cinema.api.application.dto.movie;

import com.example.cinema.api.domain.movie.AudioType;
import com.example.cinema.api.domain.movie.MovieExhibition;
import com.example.cinema.api.domain.movie.MovieFormat;
import com.example.cinema.api.domain.movie.MovieRating;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MovieExhibitionCardDTO {
    private Long id;
    private String title;
    private Long movieId;
    private String movieTitle;
    private String posterUrl;
    private MovieRating rating;
    private MovieFormat format;
    private AudioType audio;
    private boolean preRelease;
    private int duration;
    private String genre;

    public MovieExhibitionCardDTO(MovieExhibition exhibition) {
        this.id = exhibition.getId();
        this.title = exhibition.getTitle();
        this.movieId = exhibition.getMovie().getId();
        this.movieTitle = exhibition.getMovie().getTitle();
        this.posterUrl = exhibition.getMovie().getImageUrl();
        this.rating = exhibition.getMovie().getRating();
        this.format = exhibition.getFormat();
        this.audio = exhibition.getAudio();
        this.preRelease = exhibition.getMovie().isPreRelease();
        this.duration = exhibition.getMovie().getDuration();
        this.genre = exhibition.getMovie().getGenre().getName();
    }
}
