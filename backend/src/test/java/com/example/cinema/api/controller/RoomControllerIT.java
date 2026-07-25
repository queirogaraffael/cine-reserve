package com.example.cinema.api.controller;

import org.springframework.security.test.context.support.WithMockUser;
import com.example.cinema.api.domain.cinema.Cinema;
import com.example.cinema.api.domain.room.Room;
import com.example.cinema.api.infrastructure.persistence.CinemaRepositoryJpa;
import com.example.cinema.api.infrastructure.persistence.RoomRepositoryJpa;
import com.example.cinema.api.application.dto.room.RoomRequestDTO;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.IntStream;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("integration")
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class RoomControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RoomRepositoryJpa roomRepositoryJpa;

    @Autowired
    private CinemaRepositoryJpa cinemaRepositoryJpa;

    @Autowired
    private ObjectMapper objectMapper;

    private Cinema defaultCinema;

    @BeforeEach
    void setUp() {
        roomRepositoryJpa.deleteAll();
        defaultCinema = cinemaRepositoryJpa.findById(1L)
                .orElseGet(() -> cinemaRepositoryJpa.save(new Cinema("CineReserve Matriz", "São Paulo", "SP", null)));
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void createRoom_ReturnsCreated() throws Exception {

        RoomRequestDTO dto = new RoomRequestDTO("Sala 1", defaultCinema.getId());

        mockMvc.perform(post("/api/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Sala 1")))
                .andExpect(jsonPath("$.cinemaId", is(defaultCinema.getId().intValue())));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldNotCreateRoomWithDuplicateNameInSameCinema() throws Exception {

        roomRepositoryJpa.save(new Room("Sala 1", defaultCinema));

        RoomRequestDTO room = new RoomRequestDTO("Sala 1", defaultCinema.getId());

        mockMvc.perform(post("/api/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(room)))
                .andExpect(status().isConflict());
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void getRoomById_ReturnsOk_WhenRoomExists() throws Exception {

        Room saved = roomRepositoryJpa.save(new Room("Sala 2", defaultCinema));

        mockMvc.perform(get("/api/rooms/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(saved.getId().intValue())))
                .andExpect(jsonPath("$.name", is("Sala 2")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getRoomById_ReturnsNotFound_WhenMissing() throws Exception {

        mockMvc.perform(get("/api/rooms/9999"))
                .andExpect(status().isNotFound());
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllRooms_ReturnsPagedResults() throws Exception {

        IntStream.rangeClosed(1, 3)
                .forEach(i -> roomRepositoryJpa.save(new Room("Sala " + i, defaultCinema)));

        mockMvc.perform(get("/api/rooms?page=0&size=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements", is(3)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateRoom_ReturnsOk_WhenSuccessful() throws Exception {

        Room original = roomRepositoryJpa.save(new Room("Sala 4", defaultCinema));
        RoomRequestDTO dto = new RoomRequestDTO("Sala 4 Premium", defaultCinema.getId());

        mockMvc.perform(put("/api/rooms/" + original.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Sala 4 Premium")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateRoom_ReturnsNotFound_WhenRoomMissing() throws Exception {

        RoomRequestDTO dto = new RoomRequestDTO("Sala X", defaultCinema.getId());

        mockMvc.perform(put("/api/rooms/12345")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateRoom_ReturnsConflict_WhenDuplicateName() throws Exception {

        roomRepositoryJpa.save(new Room("Sala VIP", defaultCinema));
        Room second = roomRepositoryJpa.save(new Room("Sala Premium", defaultCinema));

        RoomRequestDTO dto = new RoomRequestDTO("Sala VIP", defaultCinema.getId());

        mockMvc.perform(put("/api/rooms/" + second.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteRoom() throws Exception {

        Room room = roomRepositoryJpa.save(new Room("Room to Delete", defaultCinema));

        mockMvc.perform(delete("/api/rooms/{id}", room.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/rooms/{id}", room.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    void createRoom_ReturnsForbidden_WhenUserIsNotAdmin() throws Exception {
        RoomRequestDTO dto = new RoomRequestDTO("Sala 1", 1L);

        mockMvc.perform(post("/api/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteRoom_ReturnsUnauthorized_WhenAnonymous() throws Exception {
        mockMvc.perform(delete("/api/rooms/1"))
                .andExpect(status().isUnauthorized());
    }

}

