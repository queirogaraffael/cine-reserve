package com.example.cinema.api.controller;

import com.example.cinema.api.shared.TestUtils;
import com.example.cinema.api.infrastructure.persistence.GenreRepositoryJpa;
import com.example.cinema.api.infrastructure.persistence.MovieRepositoryJpa;
import com.example.cinema.api.infrastructure.persistence.UserRepositoryJpa;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@Tag("integration")
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Disabled
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

/*
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
                "http://image.url/inception.jpg",
                MovieRating.LIVRE,
                false
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
    void findAllPageable_ReturnsPagedResults() throws Exception {
        Genre genero = new Genre();
        genero.setName("Sci-Fi");
        Genre genre = genreRepository.save(genero);

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
    void findByTitleContainingIgnoreCase_ReturnsMatching() throws Exception {
        Genre genero = new Genre();
        genero.setName("Adventure");
        Genre genre = genreRepository.save(genero);

        movieRepositoryJpa.save(new Movie("Jurassic World", "Dinosaurs in the modern world", LocalDate.of(2015, 6, 12), 124, "http://image.url/jurassicworld.jpg", genre, MovieRating.LIVRE, false));
        movieRepositoryJpa.save(new Movie("Jumanji", "A game that brings the jungle to life", LocalDate.of(2017, 12, 20), 119, "http://image.url/jumanji.jpg", genre, MovieRating.LIVRE, false));

        mockMvc.perform(get("/api/movies/search?title=ju&page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)));
    }

    @Test
    void findByGenreId_ReturnsGenreMovies() throws Exception {
        Genre g1 = genreRepository.save(new Genre(null, "Comedy", null));
        Genre g2 = genreRepository.save(new Genre(null, "Horror", null));

        movieRepositoryJpa.save(new Movie("Funny Movie", "A hilarious comedy", LocalDate.now(), 90, "http://image.url/funny.jpg", g1, MovieRating.LIVRE, false));
        movieRepositoryJpa.save(new Movie("Scary Movie", "A terrifying horror film", LocalDate.now(), 95, "http://image.url/scary.jpg", g2, MovieRating.A16, false));

        mockMvc.perform(get("/api/movies/genre/" + g1.getId() + "?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].title", is("Funny Movie")));
    }

    @Test
    void findByTitleAndGenreId_ReturnsFiltered() throws Exception {
        Genre genre = genreRepository.save(new Genre(null, "Action", null));
        movieRepositoryJpa.save(new Movie("Avengers", "Heroes assemble", LocalDate.now(), 143, "", genre, MovieRating.A12, false));
        movieRepositoryJpa.save(new Movie("Avatar", "Another world", LocalDate.now(), 162, "", genre, MovieRating.A10, false));

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

        MovieUpdateDTO dto = new MovieUpdateDTO("Title", "Desc", LocalDate.now(), 100, "", 1L, MovieRating.LIVRE, null, null);

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
        Movie movie = movieRepositoryJpa.save(new Movie("Title", "Desc", LocalDate.now(), 100, "", genre, MovieRating.LIVRE, false));

        MovieUpdateDTO dto = new MovieUpdateDTO("Title", "Desc", LocalDate.now(), 100, "", 9999L, MovieRating.LIVRE, null, null);

        mockMvc.perform(put("/api/movies/" + movie.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

 */
}
