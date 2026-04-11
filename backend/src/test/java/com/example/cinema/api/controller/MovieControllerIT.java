package com.example.cinema.api.controller;

import com.example.cinema.api.shared.TestUtils;
import com.example.cinema.api.domain.genre.Genre;
import com.example.cinema.api.domain.movie.Movie;
import com.example.cinema.api.domain.ticket.TicketCategory;
import com.example.cinema.api.domain.user.UserRole;
import com.example.cinema.api.infrastructure.persistence.GenreRepositoryJpa;
import com.example.cinema.api.infrastructure.persistence.MovieRepositoryJpa;
import com.example.cinema.api.infrastructure.persistence.UserRepositoryJpa;
import com.example.cinema.api.application.dto.movie.MovieRequestDTO;
import com.example.cinema.api.application.dto.movie.MovieUpdateDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("integration")
@SpringBootTest
@AutoConfigureMockMvc
class MovieControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MovieRepositoryJpa movieRepositoryJpa;

    @Autowired
    private GenreRepositoryJpa genreRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestUtils testUtils;

    @Autowired
    private UserRepositoryJpa userRepositoryJpa;

    @BeforeEach
    void setup() throws Exception {
        movieRepositoryJpa.deleteAll();
        genreRepository.deleteAll();
        userRepositoryJpa.deleteAll();

    }

    @Test
    void createMovie_ReturnsCreated() throws Exception {

        String token = testUtils.authenticateAs(UserRole.ADMIN, TicketCategory.REGULAR).get("token");

        Genre genero = new Genre();
        genero.setName("Action");
        Genre genre = genreRepository.save(genero);


        MovieRequestDTO dto = new MovieRequestDTO(
                "Inception",
                "A mind-bending thriller",
                LocalDate.of(2010, 7, 16),
                148,
                "http://image.url/inception.jpg"
        );

        mockMvc.perform(post("/api/movies/" + genre.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is("Inception")))
                .andExpect(jsonPath("$.description", is("A mind-bending thriller")));
    }

    @Test
    void findById_ReturnsOk_WhenMovieExists() throws Exception {
        Genre genero = new Genre();
        genero.setName("Drama");
        Genre genre = genreRepository.save(genero);

        Movie filme = new Movie();
        filme.setTitle("The Shawshank Redemption");
        filme.setDescription("Hope can set you free");
        filme.setReleaseDate(LocalDate.of(1994, 9, 23));
        filme.setDuration(142);
        filme.setImageUrl("http://image.url/shawshank.jpg");
        filme.setGenre(genre);
        Movie movie = movieRepositoryJpa.save(filme);

        mockMvc.perform(get("/api/movies/" + movie.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(movie.getId().intValue())))
                .andExpect(jsonPath("$.title", is("The Shawshank Redemption")));
    }


    @Test
    void findAllPageable_ReturnsPagedResults() throws Exception {
        Genre genero = new Genre();
        genero.setName("Sci-Fi");
        Genre genre = genreRepository.save(genero);

        for (int i = 1; i <= 3; i++) {
            Movie filme = new Movie();
            filme.setTitle("Movie " + i);
            filme.setDescription("Description " + i);
            filme.setReleaseDate(LocalDate.of(2000 + i, 1, 1));
            filme.setDuration(100 + i);
            filme.setImageUrl("http://image.url/movie" + i + ".jpg");
            filme.setGenre(genre);

            movieRepositoryJpa.save(filme);
        }

        mockMvc.perform(get("/api/movies?page=0&size=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements", is(3)));
    }


    @Test
    void findByTitleContainingIgnoreCase_ReturnsMatching() throws Exception {
        Genre genero = new Genre();
        genero.setName("Adventure");
        Genre genre = genreRepository.save(genero);

        Movie filme1 = new Movie();
        filme1.setTitle("Jurassic World");
        filme1.setDescription("Dinosaurs in the modern world");
        filme1.setReleaseDate(LocalDate.of(2015, 6, 12));
        filme1.setDuration(124);
        filme1.setImageUrl("http://image.url/jurassicworld.jpg");
        filme1.setGenre(genre);
        movieRepositoryJpa.save(filme1);

        Movie filme2 = new Movie();
        filme2.setTitle("Jumanji");
        filme2.setDescription("A game that brings the jungle to life");
        filme2.setReleaseDate(LocalDate.of(2017, 12, 20));
        filme2.setDuration(119);
        filme2.setImageUrl("http://image.url/jumanji.jpg");
        filme2.setGenre(genre);
        movieRepositoryJpa.save(filme2);

        mockMvc.perform(get("/api/movies/search?title=ju&page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)));
    }


    @Test
    void findByGenreId_ReturnsGenreMovies() throws Exception {
        Genre g1 = new Genre();
        g1.setName("Comedy");
        genreRepository.save(g1);

        Genre g2 = new Genre();
        g2.setName("Horror");
        genreRepository.save(g2);

        Movie filme1 = new Movie();
        filme1.setTitle("Funny Movie");
        filme1.setDescription("A hilarious comedy");
        filme1.setReleaseDate(LocalDate.now());
        filme1.setDuration(90);
        filme1.setImageUrl("http://image.url/funny.jpg");
        filme1.setGenre(g1);
        movieRepositoryJpa.save(filme1);

        Movie filme2 = new Movie();
        filme2.setTitle("Scary Movie");
        filme2.setDescription("A terrifying horror film");
        filme2.setReleaseDate(LocalDate.now());
        filme2.setDuration(95);
        filme2.setImageUrl("http://image.url/scary.jpg");
        filme2.setGenre(g2);
        movieRepositoryJpa.save(filme2);

        mockMvc.perform(get("/api/movies/genre/" + g1.getId() + "?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].title", is("Funny Movie")));
    }


    @Test
    void findByTitleAndGenreId_ReturnsFiltered() throws Exception {
        Genre genre = genreRepository.save(new Genre(null, "Action", null));
        movieRepositoryJpa.save(new Movie(null, "Avengers", "", LocalDate.now(), 143, "", genre, null));
        movieRepositoryJpa.save(new Movie(null, "Avatar", "", LocalDate.now(), 162, "", genre, null));

        mockMvc.perform(get("/api/movies/search/genre/" + genre.getId() + "?title=av&page=0&size=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)));
    }


    @Test
    void updateMovie_ReturnsOk_WhenSuccessful() throws Exception {

        String token = testUtils.authenticateAs(UserRole.ADMIN, TicketCategory.REGULAR).get("token");

        Genre oldGenre = genreRepository.save(new Genre(null, "Thriller", null));
        Genre newGenre = genreRepository.save(new Genre(null, "Mystery", null));
        Movie movie = movieRepositoryJpa.save(new Movie(
                null,
                "Old Title",
                "Old Desc",
                LocalDate.of(2015, 5, 20),
                110,
                "http://image.url/old.jpg",
                oldGenre, null
        ));

        MovieUpdateDTO dto = new MovieUpdateDTO(
                "New Title",
                "New Desc",
                LocalDate.of(2020, 10, 10),
                120,
                "http://image.url/new.jpg",
                newGenre.getId()
        );

        mockMvc.perform(put("/api/movies/" + movie.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("New Title")))
                .andExpect(jsonPath("$.description", is("New Desc")));
    }

    @Test
    void findById_ReturnsNotFound_WhenMissing() throws Exception {
        mockMvc.perform(get("/api/movies/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateMovie_ReturnsNotFound_WhenMovieMissing() throws Exception {

        String token = testUtils.authenticateAs(UserRole.ADMIN, TicketCategory.REGULAR).get("token");

        MovieUpdateDTO dto = new MovieUpdateDTO(
                "Title",
                "Desc",
                LocalDate.now(),
                100,
                "",
                1L
        );

        mockMvc.perform(put("/api/movies/12345")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateMovie_ReturnsNotFound_WhenGenreMissing() throws Exception {

        String token = testUtils.authenticateAs(UserRole.ADMIN, TicketCategory.REGULAR).get("token");

        Genre genre = genreRepository.save(new Genre(null, "Original", null));
        Movie movie = movieRepositoryJpa.save(new Movie(
                null,
                "Title",
                "Desc",
                LocalDate.now(),
                100,
                "",
                genre, null
        ));

        MovieUpdateDTO dto = new MovieUpdateDTO(
                "Title",
                "Desc",
                LocalDate.now(),
                100,
                "",
                9999L
        );

        mockMvc.perform(put("/api/movies/" + movie.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

}
