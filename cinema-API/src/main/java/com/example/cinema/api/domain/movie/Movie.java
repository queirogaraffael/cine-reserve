package com.example.cinema.api.domain.movie;

import com.example.cinema.api.domain.genre.Genre;
import com.example.cinema.api.domain.movie.exception.*;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private Long id;
    private String title;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    private String imageUrl;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "genre_id")
    private Genre genre;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MovieSession> movieSessions = new ArrayList<>();

    public Movie(String title, String description, LocalDate releaseDate, int duration, String imageUrl, Genre genre) {

        if (title == null || title.isBlank()) {
            throw new MovieTitleRequiredException("O título do filme é obrigatório.");
        }

        if (description == null || description.isBlank()) {
            throw new MovieDescriptionRequiredException("A descrição do filme é obrigatória.");
        }

        if (releaseDate == null) {
            throw new ReleaseDateRequiredException("A data de lançamento é obrigatória.");
        }

        if (duration <= 0) {
            throw new InvalidMovieDurationException("A duração do filme deve ser maior que zero.");
        }

        if (genre == null) {
            throw new MovieGenreRequiredException("O gênero do filme é obrigatório.");
        }

        this.title = title;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.imageUrl = imageUrl;
        this.genre = genre;
    }
}
