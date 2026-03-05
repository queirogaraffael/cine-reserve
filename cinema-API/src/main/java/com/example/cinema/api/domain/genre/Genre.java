package com.example.cinema.api.domain.genre;

import com.example.cinema.api.domain.genre.exception.GenreNameRequiredException;
import com.example.cinema.api.domain.movie.Movie;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "genre", cascade = CascadeType.ALL)
    private List<Movie> movies = new ArrayList<>();

    public Genre(String name) {

        if (name == null || name.isBlank()) {
            throw new GenreNameRequiredException("O nome do gênero é obrigatório.");
        }

        this.name = name;
    }

}
