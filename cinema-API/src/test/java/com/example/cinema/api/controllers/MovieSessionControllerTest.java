package com.example.cinema.api.controllers;

import com.example.cinema.api.domain.entities.Movie;
import com.example.cinema.api.domain.entities.Room;
import com.example.cinema.api.domain.enums.UserCategory;
import com.example.cinema.api.domain.enums.UserRole;
import com.example.cinema.api.domain.services.MovieSessionService;
import com.example.cinema.api.infrastructure.persistence.MovieRepositoryJpa;
import com.example.cinema.api.infrastructure.persistence.RoomRepositoryJpa;
import com.example.cinema.api.infrastructure.persistence.UserRepositoryJpa;
import com.example.cinema.api.shared.dtos.movieSession.MovieSessionRequestDTO;
import com.example.cinema.api.utils.TestUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class MovieSessionControllerTest {

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


    @Test
    void testCreateMovieSessionSuccessfully() throws Exception {

        String token = testUtils.authenticateAs(UserRole.ADMIN, UserCategory.REGULAR).get("token");

        Movie movie = new Movie();
        movie.setTitle("Inception");
        movie.setDuration(148);
        movieRepositoryJpa.save(movie);

        Room room = new Room();
        room.setNumber("1");
        room.setCapacity(100);
        roomRepositoryJpa.save(room);

        MovieSessionRequestDTO dto = new MovieSessionRequestDTO();
        dto.setShowDate(LocalDate.now().plusDays(1));
        dto.setStartTime(LocalTime.of(19, 0));
        dto.setEndTime(LocalTime.of(21, 30));
        dto.setBasePrice(new BigDecimal("25.50"));
        dto.setRoomId(room.getId());
        dto.setMovieId(movie.getId());

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
                .andExpect(jsonPath("$.movieId").value(movie.getId()));
    }


    @Test
    void testCreateMovieSessionWithPastDateShouldFail() throws Exception {

        String token = testUtils.authenticateAs(UserRole.ADMIN, UserCategory.REGULAR).get("token");

        Movie movie1 = new Movie();
        movie1.setTitle("Matrix");
        movie1.setDuration(136);

        Room room1 = new Room();
        room1.setNumber("2");
        room1.setCapacity(50);

        Movie movie = movieRepositoryJpa.save(movie1);
        Room room = roomRepositoryJpa.save(room1);

        MovieSessionRequestDTO dto = new MovieSessionRequestDTO();
        dto.setShowDate(LocalDate.now().minusDays(1)); // data inválida
        dto.setStartTime(LocalTime.of(18, 0));
        dto.setEndTime(LocalTime.of(20, 0));
        dto.setBasePrice(new BigDecimal("30.00"));
        dto.setRoomId(room.getId());
        dto.setMovieId(movie.getId());

        mockMvc.perform(post("/api/sessions")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("A data da sessão não pode estar no passado")));
    }


    @Test
    void testCreateMovieSessionUnauthorizedAsRegularUser() throws Exception {

        String token = testUtils.authenticateAs(UserRole.USER, UserCategory.REGULAR).get("token");

        Movie movie1 = new Movie();
        movie1.setTitle("Avatar");
        movie1.setDuration(155);

        Room room1 = new Room();
        room1.setNumber("3");
        room1.setCapacity(80);

        Movie movie = movieRepositoryJpa.save(movie1);
        Room room = roomRepositoryJpa.save(room1);

        MovieSessionRequestDTO dto = new MovieSessionRequestDTO();
        dto.setShowDate(LocalDate.now().plusDays(1));
        dto.setStartTime(LocalTime.of(14, 0));
        dto.setEndTime(LocalTime.of(16, 30));
        dto.setBasePrice(new BigDecimal("35.00"));
        dto.setRoomId(room.getId());
        dto.setMovieId(movie.getId());

        mockMvc.perform(post("/api/sessions")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

}