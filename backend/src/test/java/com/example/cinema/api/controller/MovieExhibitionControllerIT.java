package com.example.cinema.api.controller;

import com.example.cinema.api.domain.cinema.Cinema;
import com.example.cinema.api.domain.genre.Genre;
import com.example.cinema.api.domain.movie.*;
import com.example.cinema.api.infrastructure.persistence.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("integration")
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MovieExhibitionControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CinemaRepositoryJpa cinemaRepositoryJpa;

    @Autowired
    private MovieRepositoryJpa movieRepositoryJpa;

    @Autowired
    private GenreRepositoryJpa genreRepositoryJpa;

    @Autowired
    private MovieExhibitionRepositoryJpa movieExhibitionRepositoryJpa;

    @BeforeEach
    void setup() {
        movieExhibitionRepositoryJpa.deleteAll();
        movieRepositoryJpa.deleteAll();
        genreRepositoryJpa.deleteAll();
        cinemaRepositoryJpa.deleteAll();
    }

    @Test
    void shouldListActiveExhibitionsForCinema() throws Exception {
        Cinema cinema = cinemaRepositoryJpa.save(new Cinema("Cinema Teste", "São Paulo", "SP", "logo.png"));
        Genre genre = genreRepositoryJpa.save(new Genre("Ação"));
        Movie movie = movieRepositoryJpa.save(new Movie("Batman", "Descrição", LocalDate.now(), 150, "poster.jpg", genre, MovieRating.A14, false));

        MovieExhibition ex1 = new MovieExhibition(movie, cinema, MovieFormat.F2D, AudioType.LEGENDADO);
        MovieExhibition ex2 = new MovieExhibition(movie, cinema, MovieFormat.F3D, AudioType.DUBLADO);
        movieExhibitionRepositoryJpa.save(ex1);
        movieExhibitionRepositoryJpa.save(ex2);

        mockMvc.perform(get("/api/cinemas/" + cinema.getId() + "/exhibitions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].duration").value(150))
                .andExpect(jsonPath("$[0].genre").value("Ação"));
    }

    @Test
    void shouldFilterExhibitionsByFormat() throws Exception {
        Cinema cinema = cinemaRepositoryJpa.save(new Cinema("Cinema Teste 2", "Rio de Janeiro", "RJ", "logo.png"));
        Genre genre = genreRepositoryJpa.save(new Genre("Ficção"));
        Movie movie = movieRepositoryJpa.save(new Movie("Duna", "Descrição", LocalDate.now(), 160, "poster.jpg", genre, MovieRating.A12, false));

        MovieExhibition ex1 = new MovieExhibition(movie, cinema, MovieFormat.F2D, AudioType.LEGENDADO);
        MovieExhibition ex2 = new MovieExhibition(movie, cinema, MovieFormat.F3D, AudioType.DUBLADO);
        movieExhibitionRepositoryJpa.save(ex1);
        movieExhibitionRepositoryJpa.save(ex2);

        mockMvc.perform(get("/api/cinemas/" + cinema.getId() + "/exhibitions")
                        .param("format", "F2D"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].format").value("F2D"));
    }
}
