package com.example.cinema.api.controller;

import com.example.cinema.api.application.dto.movieSession.MovieSessionRequestDTO;
import com.example.cinema.api.domain.cinema.Cinema;
import com.example.cinema.api.domain.genre.Genre;
import com.example.cinema.api.domain.movie.AudioType;
import com.example.cinema.api.domain.movie.Movie;
import com.example.cinema.api.domain.movie.MovieExhibition;
import com.example.cinema.api.domain.movie.MovieFormat;
import com.example.cinema.api.domain.movie.MovieRating;
import com.example.cinema.api.domain.movie.MovieSession;
import com.example.cinema.api.domain.room.Room;
import com.example.cinema.api.infrastructure.persistence.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import com.example.cinema.api.shared.fixtures.WithMockAuthenticatedUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Tag("integration")
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MovieSessionControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MovieRepositoryJpa movieRepositoryJpa;

    @Autowired
    private RoomRepositoryJpa roomRepositoryJpa;

    @Autowired
    private CinemaRepositoryJpa cinemaRepositoryJpa;

    @Autowired
    private GenreRepositoryJpa genreRepositoryJpa;

    @Autowired
    private MovieExhibitionRepositoryJpa movieExhibitionRepositoryJpa;

    @Autowired
    private MovieSessionRepositoryJpa movieSessionRepositoryJpa;

    private Cinema cinema;
    private Room room;
    private Genre genre;
    private Movie movie;
    private MovieExhibition exhibition;

    @BeforeEach
    void setup() {
        movieSessionRepositoryJpa.deleteAll();
        movieExhibitionRepositoryJpa.deleteAll();
        movieRepositoryJpa.deleteAll();
        genreRepositoryJpa.deleteAll();
        roomRepositoryJpa.deleteAll();
        cinemaRepositoryJpa.deleteAll();

        cinema = cinemaRepositoryJpa.save(new Cinema("Cinema Itaquera", "São Paulo", "SP", null));
        room = roomRepositoryJpa.save(new Room("Sala 1", cinema));
        genre = genreRepositoryJpa.save(new Genre("Ação"));
        movie = movieRepositoryJpa
                .save(new Movie("Matrix", "Ficção", LocalDate.now(), 130, "url", genre, MovieRating.A14, false));
        exhibition = movieExhibitionRepositoryJpa
                .save(new MovieExhibition(movie, cinema, MovieFormat.F2D, AudioType.LEGENDADO));
    }

    @AfterEach
    void tearDown() {
        movieSessionRepositoryJpa.deleteAll();
        movieExhibitionRepositoryJpa.deleteAll();
        movieRepositoryJpa.deleteAll();
        genreRepositoryJpa.deleteAll();
        roomRepositoryJpa.deleteAll();
        cinemaRepositoryJpa.deleteAll();
    }

    @Test
    @WithMockAuthenticatedUser(roles = "SUPER_ADMIN")
    void testCreateMovieSession_Success() throws Exception {
        MovieSessionRequestDTO dto = new MovieSessionRequestDTO();
        dto.setShowDate(LocalDate.now().plusDays(2));
        dto.setStartTime(LocalTime.of(14, 0));
        dto.setEndTime(LocalTime.of(16, 0));
        dto.setBasePrice(new BigDecimal("25.00"));
        dto.setRoomId(room.getId());
        dto.setExhibitionId(exhibition.getId());

        mockMvc.perform(post("/api/sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.basePrice").value(25.0));
    }

    @Test
    @WithMockAuthenticatedUser(roles = "USER")
    void testCreateMovieSession_Forbidden() throws Exception {
        MovieSessionRequestDTO dto = new MovieSessionRequestDTO();
        dto.setShowDate(LocalDate.now().plusDays(2));
        dto.setStartTime(LocalTime.of(14, 0));
        dto.setEndTime(LocalTime.of(16, 0));
        dto.setBasePrice(new BigDecimal("25.00"));
        dto.setRoomId(room.getId());
        dto.setExhibitionId(exhibition.getId());

        mockMvc.perform(post("/api/sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCreateMovieSession_Unauthorized() throws Exception {
        MovieSessionRequestDTO dto = new MovieSessionRequestDTO();

        mockMvc.perform(post("/api/sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockAuthenticatedUser(roles = "SUPER_ADMIN")
    void testCreateMovieSession_BadRequest_PastDate() throws Exception {
        MovieSessionRequestDTO dto = new MovieSessionRequestDTO();
        dto.setShowDate(LocalDate.now().minusDays(1)); // Passado
        dto.setStartTime(LocalTime.of(14, 0));
        dto.setEndTime(LocalTime.of(16, 0));
        dto.setBasePrice(new BigDecimal("25.00"));
        dto.setRoomId(room.getId());
        dto.setExhibitionId(exhibition.getId());

        mockMvc.perform(post("/api/sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockAuthenticatedUser(roles = "SUPER_ADMIN")
    void testCreateMovieSession_BadRequest_NullField() throws Exception {
        MovieSessionRequestDTO dto = new MovieSessionRequestDTO();

        mockMvc.perform(post("/api/sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockAuthenticatedUser(roles = "USER")
    void testGetMovieSessionById_Success() throws Exception {
        MovieSession session = movieSessionRepositoryJpa.save(new MovieSession(LocalDate.now().plusDays(1),
                LocalTime.of(10, 0), LocalTime.of(12, 0), new BigDecimal("20"), room, exhibition));

        mockMvc.perform(get("/api/sessions/{id}", session.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(session.getId()));
    }

    @Test
    @WithMockAuthenticatedUser(roles = "USER")
    void testGetMovieSessionById_NotFound() throws Exception {
        mockMvc.perform(get("/api/sessions/{id}", 99999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockAuthenticatedUser(roles = "USER")
    void testGetAvailableSeats_Success() throws Exception {
        MovieSession session = movieSessionRepositoryJpa.save(new MovieSession(LocalDate.now().plusDays(1),
                LocalTime.of(10, 0), LocalTime.of(12, 0), new BigDecimal("20"), room, exhibition));

        mockMvc.perform(get("/api/sessions/{id}/seats", session.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void testGetSessionsByExhibition_Success() throws Exception {
        mockMvc.perform(get("/api/exhibitions/{exhibitionId}/sessions", exhibition.getId()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockAuthenticatedUser(roles = "USER")
    void testGetTicketTypes_Success() throws Exception {
        MovieSession session = movieSessionRepositoryJpa.save(new MovieSession(LocalDate.now().plusDays(1),
                LocalTime.of(10, 0), LocalTime.of(12, 0), new BigDecimal("20"), room, exhibition));

        mockMvc.perform(get("/api/sessions/{id}/ticket-types", session.getId()))
                .andExpect(status().isOk());
    }
}