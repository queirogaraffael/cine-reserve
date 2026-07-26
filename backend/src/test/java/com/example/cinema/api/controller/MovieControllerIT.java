package com.example.cinema.api.controller;

import org.springframework.security.test.context.support.WithMockUser;
import com.example.cinema.api.domain.genre.Genre;
import com.example.cinema.api.domain.movie.Movie;
import com.example.cinema.api.domain.movie.MovieRating;
import com.example.cinema.api.infrastructure.persistence.GenreRepositoryJpa;
import com.example.cinema.api.infrastructure.persistence.MovieRepositoryJpa;
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
import org.springframework.test.context.ActiveProfiles;
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
@ActiveProfiles("test")
class MovieControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MovieRepositoryJpa movieRepositoryJpa;

    @Autowired
    private GenreRepositoryJpa genreRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() throws Exception {
        movieRepositoryJpa.deleteAll();
        genreRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createMovie_ReturnsCreated() throws Exception {

        Genre genre = genreRepository.save(new Genre("Action"));

        MovieRequestDTO dto = new MovieRequestDTO(
                "Inception",
                "A mind-bending thriller",
                LocalDate.of(2010, 7, 16),
                148,
                "http://image.url/inception.jpg",
                MovieRating.LIVRE,
                false,
                genre.getId()
        );

        mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is("Inception")))
                .andExpect(jsonPath("$.description", is("A mind-bending thriller")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void findById_ReturnsOk_WhenMovieExists() throws Exception {
        Genre genre = genreRepository.save(new Genre("Drama"));

        Movie saved = movieRepositoryJpa.save(new Movie(
                "The Shawshank Redemption",
                "Hope can set you free",
                LocalDate.of(1994, 9, 23),
                142,
                "http://image.url/shawshank.jpg",
                genre,
                MovieRating.LIVRE,
                false
        ));

        mockMvc.perform(get("/api/movies/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(saved.getId().intValue())))
                .andExpect(jsonPath("$.title", is("The Shawshank Redemption")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void findAllPageable_ReturnsPagedResults() throws Exception {
        Genre genre = genreRepository.save(new Genre("Sci-Fi"));

        for (int i = 1; i <= 3; i++) {
            movieRepositoryJpa.save(new Movie(
                    "Movie " + i,
                    "Description " + i,
                    LocalDate.of(2000 + i, 1, 1),
                    100 + i,
                    "http://image.url/movie" + i + ".jpg",
                    genre,
                    MovieRating.LIVRE,
                    false
            ));
        }

        mockMvc.perform(get("/api/movies?page=0&size=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements", is(3)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void findByTitleContainingIgnoreCase_ReturnsMatching() throws Exception {
        Genre genre = genreRepository.save(new Genre("Adventure"));

        movieRepositoryJpa.save(new Movie("Jurassic World", "Dinosaurs in the modern world", LocalDate.of(2015, 6, 12), 124, "http://image.url/jurassicworld.jpg", genre, MovieRating.LIVRE, false));
        movieRepositoryJpa.save(new Movie("Jumanji", "A game that brings the jungle to life", LocalDate.of(2017, 12, 20), 119, "http://image.url/jumanji.jpg", genre, MovieRating.LIVRE, false));

        mockMvc.perform(get("/api/movies/search?title=ju&page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void findByGenreId_ReturnsGenreMovies() throws Exception {
        Genre g1 = genreRepository.save(new Genre("Comedy"));
        Genre g2 = genreRepository.save(new Genre("Horror"));

        movieRepositoryJpa.save(new Movie("Funny Movie", "A hilarious comedy", LocalDate.now(), 90, "http://image.url/funny.jpg", g1, MovieRating.LIVRE, false));
        movieRepositoryJpa.save(new Movie("Scary Movie", "A terrifying horror film", LocalDate.now(), 95, "http://image.url/scary.jpg", g2, MovieRating.A16, false));

        mockMvc.perform(get("/api/movies/genre/" + g1.getId() + "?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].title", is("Funny Movie")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void findByTitleAndGenreId_ReturnsFiltered() throws Exception {
        Genre genre = genreRepository.save(new Genre("Action"));
        movieRepositoryJpa.save(new Movie("Avengers", "Heroes assemble", LocalDate.now(), 143, "", genre, MovieRating.A12, false));
        movieRepositoryJpa.save(new Movie("Avatar", "Another world", LocalDate.now(), 162, "", genre, MovieRating.A10, false));

        mockMvc.perform(get("/api/movies/search/genre/" + genre.getId() + "?title=av&page=0&size=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateMovie_ReturnsOk_WhenSuccessful() throws Exception {

        Genre oldGenre = genreRepository.save(new Genre("Thriller"));
        Genre newGenre = genreRepository.save(new Genre("Mystery"));
        Movie movie = movieRepositoryJpa.save(new Movie(
                "Old Title",
                "Old Desc",
                LocalDate.of(2015, 5, 20),
                110,
                "http://image.url/old.jpg",
                oldGenre,
                MovieRating.LIVRE,
                false
        ));

        MovieUpdateDTO dto = new MovieUpdateDTO(
                "New Title",
                "New Desc",
                LocalDate.of(2020, 10, 10),
                120,
                "http://image.url/new.jpg",
                newGenre.getId(),
                MovieRating.A12,
                null,
                null
        );

        mockMvc.perform(put("/api/movies/" + movie.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("New Title")))
                .andExpect(jsonPath("$.description", is("New Desc")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void findById_ReturnsNotFound_WhenMissing() throws Exception {
        mockMvc.perform(get("/api/movies/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateMovie_ReturnsNotFound_WhenMovieMissing() throws Exception {

        MovieUpdateDTO dto = new MovieUpdateDTO("Title", "Desc", LocalDate.now(), 100, "", 1L, MovieRating.LIVRE, null, null);

        mockMvc.perform(put("/api/movies/12345")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateMovie_ReturnsNotFound_WhenGenreMissing() throws Exception {

        Genre genre = genreRepository.save(new Genre("Original"));
        Movie movie = movieRepositoryJpa.save(new Movie("Title", "Desc", LocalDate.now(), 100, "", genre, MovieRating.LIVRE, false));

        MovieUpdateDTO dto = new MovieUpdateDTO("Title", "Desc", LocalDate.now(), 100, "", 9999L, MovieRating.LIVRE, null, null);

        mockMvc.perform(put("/api/movies/" + movie.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteMovie() throws Exception {

        Genre genre = genreRepository.save(new Genre("Delete Genre"));
        Movie movie = movieRepositoryJpa.save(new Movie("Title", "Desc", LocalDate.now(), 100, "", genre, MovieRating.LIVRE, false));

        mockMvc.perform(delete("/api/movies/{id}", movie.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/movies/{id}", movie.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    void createMovie_ReturnsForbidden_WhenUserIsNotAdmin() throws Exception {
        MovieRequestDTO dto = new MovieRequestDTO(
                "Inception", "A mind-bending thriller", LocalDate.of(2010, 7, 16),
                148, "http://image.url/inception.jpg", MovieRating.LIVRE, false, 1L
        );

        mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteMovie_ReturnsUnauthorized_WhenAnonymous() throws Exception {
        mockMvc.perform(delete("/api/movies/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getMovies_ReturnsForbidden_WhenUserIsNotAdmin() throws Exception {
        mockMvc.perform(get("/api/movies"))
                .andExpect(status().isForbidden());
    }

}
