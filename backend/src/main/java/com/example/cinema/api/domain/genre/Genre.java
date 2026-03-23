package com.example.cinema.api.domain.genre;

import com.example.cinema.api.domain.genre.exception.GenreNameRequiredException;
import com.example.cinema.api.domain.movie.Movie;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "genre", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @Setter(AccessLevel.NONE)
    private List<Movie> movies = new ArrayList<>();

    public Genre(String name) {

        if (name == null || name.isBlank()) {
            throw new GenreNameRequiredException("O nome do gênero é obrigatório.");
        }

        this.name = name;
    }

    public void addMovie(Movie movie) {
        movies.add(movie);
        movie.setGenre(this);
    }

}
