package com.example.cinema.api.controller;

import com.example.cinema.api.application.dto.movieSession.MovieSessionRequestDTO;
import com.example.cinema.api.application.service.MovieSessionService;
import com.example.cinema.api.domain.cinema.Cinema;
import com.example.cinema.api.domain.genre.Genre;
import com.example.cinema.api.domain.movie.AudioType;
import com.example.cinema.api.domain.movie.Movie;
import com.example.cinema.api.domain.movie.MovieExhibition;
import com.example.cinema.api.domain.movie.MovieFormat;
import com.example.cinema.api.domain.movie.MovieRating;
import com.example.cinema.api.domain.room.Room;
import com.example.cinema.api.infrastructure.persistence.CinemaRepositoryJpa;
import com.example.cinema.api.infrastructure.persistence.GenreRepositoryJpa;
import com.example.cinema.api.infrastructure.persistence.MovieExhibitionRepositoryJpa;
import com.example.cinema.api.infrastructure.persistence.MovieRepositoryJpa;
import com.example.cinema.api.infrastructure.persistence.MovieSessionRepositoryJpa;
import com.example.cinema.api.infrastructure.persistence.RoomRepositoryJpa;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("integration")
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MovieSessionExhibitionControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MovieRepositoryJpa movieRepositoryJpa;

    @Autowired
    private RoomRepositoryJpa roomRepositoryJpa;

    @Autowired
    private MovieSessionRepositoryJpa movieSessionRepositoryJpa;

    @Autowired
    private MovieExhibitionRepositoryJpa movieExhibitionRepositoryJpa;

    @Autowired
    private CinemaRepositoryJpa cinemaRepositoryJpa;

    @Autowired
    private GenreRepositoryJpa genreRepositoryJpa;

    @Autowired
    private MovieSessionService movieSessionService;

    private Long exhibitionId;
    private Long roomId;

    @BeforeEach
    void setup() {
        movieSessionRepositoryJpa.deleteAll();
        movieExhibitionRepositoryJpa.deleteAll();
        movieRepositoryJpa.deleteAll();
        roomRepositoryJpa.deleteAll();
        cinemaRepositoryJpa.deleteAll();
        genreRepositoryJpa.deleteAll();

        Genre genre = genreRepositoryJpa.save(new Genre("Ação"));
        Cinema cinema = cinemaRepositoryJpa.save(new Cinema("Cinema Teste", "São Paulo", "SP", null));
        Movie movie = movieRepositoryJpa.save(new Movie(
                "Inception", "A dream within a dream.", LocalDate.of(2010, 7, 16),
                148, "https://poster.url/inception.jpg", genre, MovieRating.A14, false));

        Room room = roomRepositoryJpa.save(new Room("SALA 1", cinema));
        roomId = room.getId();

        MovieExhibition exhibition = movieExhibitionRepositoryJpa.save(
                new MovieExhibition(movie, cinema, MovieFormat.F2D, AudioType.DUBLADO));
        exhibitionId = exhibition.getId();
    }

    @AfterEach
    void tearDown() {
        movieSessionRepositoryJpa.deleteAll();
        movieExhibitionRepositoryJpa.deleteAll();
        movieRepositoryJpa.deleteAll();
        roomRepositoryJpa.deleteAll();
        cinemaRepositoryJpa.deleteAll();
        genreRepositoryJpa.deleteAll();
    }

    private void createSession(LocalDate date, LocalTime start, LocalTime end) {
        MovieSessionRequestDTO dto = new MovieSessionRequestDTO();
        dto.setShowDate(date);
        dto.setStartTime(start);
        dto.setEndTime(end);
        dto.setBasePrice(new BigDecimal("25.00"));
        dto.setRoomId(roomId);
        dto.setExhibitionId(exhibitionId);
        movieSessionService.createSession(dto);
    }

    @Test
    void shouldReturnSessionsGroupedByDayAndRoom() throws Exception {
        createSession(LocalDate.now().plusDays(1), LocalTime.of(18, 0), LocalTime.of(20, 30));

        mockMvc.perform(get("/api/sessions").param("exhibitionId", exhibitionId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exhibitionId").value(exhibitionId))
                .andExpect(jsonPath("$.format").value("F2D"))
                .andExpect(jsonPath("$.audio").value("DUBLADO"))
                .andExpect(jsonPath("$.movieDetails.title").value("Inception"))
                .andExpect(jsonPath("$.movieDetails.description").value("A dream within a dream."))
                .andExpect(jsonPath("$.days").isArray())
                .andExpect(jsonPath("$.days[0].rooms[0].roomName").value("SALA 1"))
                .andExpect(jsonPath("$.days[0].rooms[0].sessions[0].status").value("AVAILABLE"));
    }

    @Test
    void shouldReturnUnavailableStatusForSessionWithinCutoffWindow() throws Exception {
        createSession(LocalDate.now(), LocalTime.now().plusMinutes(30), LocalTime.now().plusMinutes(150));

        mockMvc.perform(get("/api/sessions").param("exhibitionId", exhibitionId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.days[0].rooms[0].sessions[0].status").value("UNAVAILABLE"));
    }

    @Test
    void shouldNotReturnCancelledSessions() throws Exception {
        createSession(LocalDate.now().plusDays(1), LocalTime.of(20, 0), LocalTime.of(22, 0));
        movieSessionRepositoryJpa.findAll().forEach(s -> {
            s.setCanceled(true);
            movieSessionRepositoryJpa.save(s);
        });

        mockMvc.perform(get("/api/sessions").param("exhibitionId", exhibitionId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.days").isEmpty());
    }

    @Test
    void shouldReturnEmptyDaysWhenNoSessionsExistForExhibition() throws Exception {
        mockMvc.perform(get("/api/sessions").param("exhibitionId", exhibitionId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.days").isEmpty());
    }

    @Test
    void shouldReturn404WhenExhibitionDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/sessions").param("exhibitionId", "99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldBeAccessibleWithoutJwtToken() throws Exception {
        mockMvc.perform(get("/api/sessions").param("exhibitionId", exhibitionId.toString()))
                .andExpect(status().isOk());
    }

    @Test
    void shouldPopulateMovieDetailsCorrectly() throws Exception {
        createSession(LocalDate.now().plusDays(2), LocalTime.of(15, 0), LocalTime.of(17, 0));

        mockMvc.perform(get("/api/sessions").param("exhibitionId", exhibitionId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.movieDetails.description").value("A dream within a dream."))
                .andExpect(jsonPath("$.movieDetails.duration").value(148))
                .andExpect(jsonPath("$.movieDetails.rating").value("A14"))
                .andExpect(jsonPath("$.movieDetails.genre").value("Ação"));
    }
}
