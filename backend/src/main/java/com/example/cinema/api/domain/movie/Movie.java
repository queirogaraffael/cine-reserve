package com.example.cinema.api.domain.movie;

import com.example.cinema.api.domain.genre.Genre;
import com.example.cinema.api.domain.movie.exception.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private Long id;

    @Setter
    private String title;
    @Setter
    private String description;
    @Setter
    private LocalDate releaseDate;
    @Setter
    private int duration;
    @Setter
    private String imageUrl;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MovieRating rating;

    @Column(nullable = false)
    private boolean inTheaters = true;

    @Column(nullable = false)
    private boolean preRelease = false;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id", nullable = false)
    @ToString.Exclude
    private Genre genre;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL)
    @ToString.Exclude
    @Setter(AccessLevel.NONE)
    private List<MovieExhibition> exhibitions = new ArrayList<>();

    public Movie(String title, String description, LocalDate releaseDate, int duration, String imageUrl, Genre genre, MovieRating rating, boolean preRelease) {
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
        if (rating == null) {
            throw new MovieRatingRequiredException("A classificação indicativa é obrigatória.");
        }
        this.title = title;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.imageUrl = imageUrl;
        this.rating = rating;
        this.inTheaters = true;
        this.preRelease = preRelease;
        genre.addMovie(this);
    }

    public void addExhibition(MovieExhibition exhibition) {
        if (!exhibitions.contains(exhibition)) {
            exhibitions.add(exhibition);
        }
    }

    public void putInTheaters() {
        this.inTheaters = true;
    }

    public void takeOffTheaters() {
        this.inTheaters = false;
    }

    public void setPreReleaseStatus(boolean status) {
        this.preRelease = status;
    }
}
