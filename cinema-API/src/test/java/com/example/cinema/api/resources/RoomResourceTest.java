package com.example.cinema.api.resources;


import com.example.cinema.api.domain.entities.Room;
import com.example.cinema.api.domain.entities.User;
import com.example.cinema.api.domain.enums.UserCategory;
import com.example.cinema.api.domain.enums.UserRole;
import com.example.cinema.api.infrastructure.repositories.RoomRepository;
import com.example.cinema.api.infrastructure.repositories.UserRepository;
import com.example.cinema.api.shared.dtos.login.UserLoginDTO;
import com.example.cinema.api.shared.dtos.room.RoomRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
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
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    private String jwtToken;

    @BeforeEach
    void setUp() {
        roomRepository.deleteAll();
        userRepository.deleteAll();
    }


    @Test
    void createRoom_ReturnsCreated() throws Exception {

        jwtToken = authenticateAs(UserRole.ADMIN);

        RoomRequestDTO dto = new RoomRequestDTO("101", 2);

        mockMvc.perform(post("/api/rooms")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.number", is("101")))
                .andExpect(jsonPath("$.capacity", is(2)));
    }

    @Test
    void shouldNotCreateRoomWithDuplicateNumber() throws Exception {

        jwtToken = authenticateAs(UserRole.ADMIN);

        roomRepository.save(new Room(null, "101", 2, null));

        RoomRequestDTO room = new RoomRequestDTO("101", 5);

        mockMvc.perform(post("/api/rooms")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(room)))
                .andExpect(status().isConflict());
    }


    @Test
    void getRoomById_ReturnsOk_WhenRoomExists() throws Exception {

        jwtToken = authenticateAs(UserRole.ADMIN);

        Room saved = roomRepository.save(new Room(null, "202", 4, null));

        mockMvc.perform(get("/api/rooms/" + saved.getId()).header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(saved.getId().intValue())))
                .andExpect(jsonPath("$.number", is("202")));
    }

    @Test
    void getRoomById_ReturnsNotFound_WhenMissing() throws Exception {

        jwtToken = authenticateAs(UserRole.ADMIN);

        mockMvc.perform(get("/api/rooms/9999").header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }


    @Test
    void getAllRooms_ReturnsPagedResults() throws Exception {

        jwtToken = authenticateAs(UserRole.ADMIN);

        IntStream.rangeClosed(1, 3)
                .forEach(i -> roomRepository.save(new Room(null, String.valueOf(300 + i), i, null)));

        mockMvc.perform(get("/api/rooms?page=0&size=2").header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements", is(3)));
    }

    @Test
    void updateRoom_ReturnsOk_WhenSuccessful() throws Exception {

        jwtToken = authenticateAs(UserRole.ADMIN);

        Room original = roomRepository.save(new Room(null, "401", 3, null));
        RoomRequestDTO dto = new RoomRequestDTO("402", 5);

        mockMvc.perform(put("/api/rooms/" + original.getId())
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number", is("402")))
                .andExpect(jsonPath("$.capacity", is(5)));
    }

    @Test
    void updateRoom_ReturnsNotFound_WhenRoomMissing() throws Exception {

        jwtToken = authenticateAs(UserRole.ADMIN);

        RoomRequestDTO dto = new RoomRequestDTO("501", 2);

        mockMvc.perform(put("/api/rooms/12345")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateRoom_ReturnsServerError_WhenDuplicateNumber() throws Exception {

        jwtToken = authenticateAs(UserRole.ADMIN);

        roomRepository.save(new Room(null, "601", 2, null));
        Room second = roomRepository.save(new Room(null, "602", 3, null));

        RoomRequestDTO dto = new RoomRequestDTO("601", 3);

        mockMvc.perform(put("/api/rooms/" + second.getId())
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }

    private String authenticateAs(UserRole role) throws Exception {
        String username = "user_" + role.name().toLowerCase();

        User user = new User();
        user.setUsername(username);
        user.setName("Test " + role.name());
        user.setEmail(username + "@test.com");
        user.setPassword(passwordEncoder.encode("senha123"));
        user.setDataJoined(LocalDate.parse("2024-01-01"));
        user.setBirthdate(LocalDate.parse("1990-01-01"));
        user.setRole(role);
        user.setCategory(UserCategory.REGULAR);

        userRepository.save(user);

        var loginDTO = new UserLoginDTO(username, "senha123");

        var result = mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        return objectMapper.readTree(response).get("token").asText();
    }
}

