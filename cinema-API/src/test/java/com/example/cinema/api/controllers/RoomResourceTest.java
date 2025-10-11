package com.example.cinema.api.controllers;


import com.example.cinema.api.domain.entities.Room;
import com.example.cinema.api.domain.enums.UserCategory;
import com.example.cinema.api.domain.enums.UserRole;
import com.example.cinema.api.infrastructure.repositories.RoomRepository;
import com.example.cinema.api.infrastructure.repositories.UserRepository;
import com.example.cinema.api.shared.dtos.room.RoomRequestDTO;
import com.example.cinema.api.utils.TestUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.IntStream;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RoomResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestUtils testUtils;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        roomRepository.deleteAll();
        userRepository.deleteAll();
    }


    @Test
    void createRoom_ReturnsCreated() throws Exception {

        String token = testUtils.authenticateAs(UserRole.ADMIN, UserCategory.REGULAR);

        RoomRequestDTO dto = new RoomRequestDTO("101", 2);

        mockMvc.perform(post("/api/rooms")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.number", is("101")))
                .andExpect(jsonPath("$.capacity", is(2)));
    }

    @Test
    void shouldNotCreateRoomWithDuplicateNumber() throws Exception {

        String token = testUtils.authenticateAs(UserRole.ADMIN, UserCategory.REGULAR);

        roomRepository.save(new Room(null, "101", 2, null));

        RoomRequestDTO room = new RoomRequestDTO("101", 5);

        mockMvc.perform(post("/api/rooms")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(room)))
                .andExpect(status().isConflict());
    }


    @Test
    void getRoomById_ReturnsOk_WhenRoomExists() throws Exception {

        String token = testUtils.authenticateAs(UserRole.ADMIN, UserCategory.REGULAR);

        Room saved = roomRepository.save(new Room(null, "202", 4, null));

        mockMvc.perform(get("/api/rooms/" + saved.getId()).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(saved.getId().intValue())))
                .andExpect(jsonPath("$.number", is("202")));
    }

    @Test
    void getRoomById_ReturnsNotFound_WhenMissing() throws Exception {

        String token = testUtils.authenticateAs(UserRole.ADMIN, UserCategory.REGULAR);

        mockMvc.perform(get("/api/rooms/9999").header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }


    @Test
    void getAllRooms_ReturnsPagedResults() throws Exception {

        String token = testUtils.authenticateAs(UserRole.ADMIN, UserCategory.REGULAR);

        IntStream.rangeClosed(1, 3)
                .forEach(i -> roomRepository.save(new Room(null, String.valueOf(300 + i), i, null)));

        mockMvc.perform(get("/api/rooms?page=0&size=2").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements", is(3)));
    }

    @Test
    void updateRoom_ReturnsOk_WhenSuccessful() throws Exception {

        String token = testUtils.authenticateAs(UserRole.ADMIN, UserCategory.REGULAR);

        Room original = roomRepository.save(new Room(null, "401", 3, null));
        RoomRequestDTO dto = new RoomRequestDTO("402", 5);

        mockMvc.perform(put("/api/rooms/" + original.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number", is("402")))
                .andExpect(jsonPath("$.capacity", is(5)));
    }

    @Test
    void updateRoom_ReturnsNotFound_WhenRoomMissing() throws Exception {

        String token = testUtils.authenticateAs(UserRole.ADMIN, UserCategory.REGULAR);

        RoomRequestDTO dto = new RoomRequestDTO("501", 2);

        mockMvc.perform(put("/api/rooms/12345")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateRoom_ReturnsServerError_WhenDuplicateNumber() throws Exception {

        String token = testUtils.authenticateAs(UserRole.ADMIN, UserCategory.REGULAR);

        roomRepository.save(new Room(null, "601", 2, null));
        Room second = roomRepository.save(new Room(null, "602", 3, null));

        RoomRequestDTO dto = new RoomRequestDTO("601", 3);

        mockMvc.perform(put("/api/rooms/" + second.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }

}

