package com.example.cinema.api.controller;

import com.example.cinema.api.shared.TestUtils;
import com.example.cinema.api.domain.movie.Movie;
import com.example.cinema.api.domain.room.Room;
import com.example.cinema.api.domain.ticket.TicketCategory;
import com.example.cinema.api.domain.user.UserRole;
import com.example.cinema.api.application.service.MovieSessionService;
import com.example.cinema.api.infrastructure.persistence.MovieRepositoryJpa;
import com.example.cinema.api.infrastructure.persistence.RoomRepositoryJpa;
import com.example.cinema.api.infrastructure.persistence.UserRepositoryJpa;
import com.example.cinema.api.application.dto.movieSession.MovieSessionRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Tag("integration")
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Disabled
class MovieSessionControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MovieRepositoryJpa movieRepositoryJpa;

    @Autowired
    private RoomRepositoryJpa roomRepositoryJpa;

    @Autowired
    private MovieSessionService movieSessionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestUtils testUtils;

    @Autowired
    private UserRepositoryJpa userRepositoryJpa;

    @BeforeEach
    void setup() throws Exception {
        userRepositoryJpa.deleteAll();
        movieRepositoryJpa.deleteAll();
        roomRepositoryJpa.deleteAll();
    }

    @AfterEach
    void tearDown() {
        userRepositoryJpa.deleteAll();
        movieRepositoryJpa.deleteAll();
        roomRepositoryJpa.deleteAll();
    }

/*
    @Test
    void testCreateMovieSessionSuccessfully() throws Exception {

        String token = testUtils.authenticateAs(UserRole.ADMIN, TicketCategory.REGULAR).get("token");

        com.example.cinema.api.domain.genre.Genre genre = new com.example.cinema.api.domain.genre.Genre(null, "Action", null);
        genreRepository.save(genre);

        Movie movie = new Movie("Inception", "Desc", LocalDate.now(), 148, "", genre, com.example.cinema.api.domain.movie.MovieRating.LIVRE, false);
        movieRepositoryJpa.save(movie);

        com.example.cinema.api.domain.cinema.Cinema cinema = new com.example.cinema.api.domain.cinema.Cinema("Cinema Matriz", "São Paulo", "SP", null);
        cinemaRepositoryJpa.save(cinema);

        Room room = new Room("Sala 1", 100, cinema);
        roomRepositoryJpa.save(room);

        MovieExhibition exhibition = new MovieExhibition(movie, cinema, MovieFormat.F2D, AudioType.LEGENDADO);
        movieExhibitionRepositoryJpa.save(exhibition);

        MovieSessionRequestDTO dto = new MovieSessionRequestDTO();
        dto.setShowDate(LocalDate.now().plusDays(1));
        dto.setStartTime(LocalTime.of(19, 0));
        dto.setEndTime(LocalTime.of(21, 30));
        dto.setBasePrice(new BigDecimal("25.50"));
        dto.setRoomId(room.getId());
        dto.setExhibitionId(exhibition.getId());

        mockMvc.perform(post("/api/sessions")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.showDate").value(dto.getShowDate().toString()))
                .andExpect(jsonPath("$.startTime").value(dto.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"))))
                .andExpect(jsonPath("$.endTime").value(dto.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"))))
                .andExpect(jsonPath("$.basePrice").value(dto.getBasePrice().doubleValue()))
                .andExpect(jsonPath("$.status").value("SCHEDULED"))
                .andExpect(jsonPath("$.roomId").value(room.getId()))
                .andExpect(jsonPath("$.exhibitionId").value(exhibition.getId()));
    }


    @Test
    void testCreateMovieSessionWithPastDateShouldFail() throws Exception {

        String token = testUtils.authenticateAs(UserRole.ADMIN, TicketCategory.REGULAR).get("token");

        com.example.cinema.api.domain.genre.Genre genre = new com.example.cinema.api.domain.genre.Genre(null, "Sci-Fi", null);
        genreRepository.save(genre);

        Movie movie = new Movie("Matrix", "Desc", LocalDate.now(), 136, "", genre, com.example.cinema.api.domain.movie.MovieRating.A14, false);
        movieRepositoryJpa.save(movie);

        com.example.cinema.api.domain.cinema.Cinema cinema = new com.example.cinema.api.domain.cinema.Cinema("Cinema 2", "Rio de Janeiro", "RJ", null);
        cinemaRepositoryJpa.save(cinema);

        Room room = new Room("Sala 2", 50, cinema);
        roomRepositoryJpa.save(room);

        MovieExhibition exhibition = new MovieExhibition(movie, cinema, MovieFormat.F2D, AudioType.DUBLADO);
        movieExhibitionRepositoryJpa.save(exhibition);

        MovieSessionRequestDTO dto = new MovieSessionRequestDTO();
        dto.setShowDate(LocalDate.now().minusDays(1)); // data inválida
        dto.setStartTime(LocalTime.of(18, 0));
        dto.setEndTime(LocalTime.of(20, 0));
        dto.setBasePrice(new BigDecimal("30.00"));
        dto.setRoomId(room.getId());
        dto.setExhibitionId(exhibition.getId());

        mockMvc.perform(post("/api/sessions")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("A data da sessão não pode estar no passado")));
    }


    @Test
    void testCreateMovieSessionUnauthorizedAsRegularUser() throws Exception {

        String token = testUtils.authenticateAs(UserRole.USER, TicketCategory.REGULAR).get("token");

        com.example.cinema.api.domain.genre.Genre genre = new com.example.cinema.api.domain.genre.Genre(null, "Adventure", null);
        genreRepository.save(genre);

        Movie movie = new Movie("Avatar", "Desc", LocalDate.now(), 155, "", genre, com.example.cinema.api.domain.movie.MovieRating.LIVRE, false);
        movieRepositoryJpa.save(movie);

        com.example.cinema.api.domain.cinema.Cinema cinema = new com.example.cinema.api.domain.cinema.Cinema("Cinema 3", "Curitiba", "PR", null);
        cinemaRepositoryJpa.save(cinema);

        Room room = new Room("Sala 3", 80, cinema);
        roomRepositoryJpa.save(room);

        MovieExhibition exhibition = new MovieExhibition(movie, cinema, MovieFormat.F3D, AudioType.DUBLADO);
        movieExhibitionRepositoryJpa.save(exhibition);

        MovieSessionRequestDTO dto = new MovieSessionRequestDTO();
        dto.setShowDate(LocalDate.now().plusDays(1));
        dto.setStartTime(LocalTime.of(14, 0));
        dto.setEndTime(LocalTime.of(16, 30));
        dto.setBasePrice(new BigDecimal("35.00"));
        dto.setRoomId(room.getId());
        dto.setExhibitionId(exhibition.getId());

        mockMvc.perform(post("/api/sessions")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }
*/
}