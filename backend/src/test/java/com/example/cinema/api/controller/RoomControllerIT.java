package com.example.cinema.api.controller;


import com.example.cinema.api.shared.TestUtils;
import com.example.cinema.api.domain.cinema.Cinema;
import com.example.cinema.api.infrastructure.persistence.CinemaRepositoryJpa;
import com.example.cinema.api.infrastructure.persistence.RoomRepositoryJpa;
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
class RoomControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RoomRepositoryJpa roomRepositoryJpa;

    @Autowired
    private CinemaRepositoryJpa cinemaRepositoryJpa;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestUtils testUtils;

    @Autowired
    private UserRepositoryJpa userRepositoryJpa;

    private Cinema defaultCinema;

    @BeforeEach
    void setUp() {
        roomRepositoryJpa.deleteAll();
        userRepositoryJpa.deleteAll();
        defaultCinema = cinemaRepositoryJpa.findById(1L)
                .orElseGet(() -> cinemaRepositoryJpa.save(new Cinema("CineReserve Matriz", "São Paulo", "SP", null)));
    }

/*
    @Test
    void createRoom_ReturnsCreated() throws Exception {

        String token = testUtils.authenticateAs(UserRole.ADMIN, TicketCategory.REGULAR).get("token");

        RoomRequestDTO dto = new RoomRequestDTO("Sala 1", 2, defaultCinema.getId());

        mockMvc.perform(post("/api/rooms")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Sala 1")))
                .andExpect(jsonPath("$.capacity", is(2)))
                .andExpect(jsonPath("$.cinemaId", is(defaultCinema.getId().intValue())));
    }

    @Test
    void shouldNotCreateRoomWithDuplicateNameInSameCinema() throws Exception {

        String token = testUtils.authenticateAs(UserRole.ADMIN, TicketCategory.REGULAR).get("token");

        roomRepositoryJpa.save(new Room("Sala 1", 2, defaultCinema));

        RoomRequestDTO room = new RoomRequestDTO("Sala 1", 5, defaultCinema.getId());

        mockMvc.perform(post("/api/rooms")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(room)))
                .andExpect(status().isConflict());
    }


    @Test
    void getRoomById_ReturnsOk_WhenRoomExists() throws Exception {

        String token = testUtils.authenticateAs(UserRole.ADMIN, TicketCategory.REGULAR).get("token");

        Room saved = roomRepositoryJpa.save(new Room("Sala 2", 4, defaultCinema));

        mockMvc.perform(get("/api/rooms/" + saved.getId()).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(saved.getId().intValue())))
                .andExpect(jsonPath("$.name", is("Sala 2")));
    }

    @Test
    void getRoomById_ReturnsNotFound_WhenMissing() throws Exception {

        String token = testUtils.authenticateAs(UserRole.ADMIN, TicketCategory.REGULAR).get("token");

        mockMvc.perform(get("/api/rooms/9999").header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }


    @Test
    void getAllRooms_ReturnsPagedResults() throws Exception {

        String token = testUtils.authenticateAs(UserRole.ADMIN, TicketCategory.REGULAR).get("token");

        IntStream.rangeClosed(1, 3)
                .forEach(i -> roomRepositoryJpa.save(new Room("Sala " + i, i, defaultCinema)));

        mockMvc.perform(get("/api/rooms?page=0&size=2").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements", is(3)));
    }

    @Test
    void updateRoom_ReturnsOk_WhenSuccessful() throws Exception {

        String token = testUtils.authenticateAs(UserRole.ADMIN, TicketCategory.REGULAR).get("token");

        Room original = roomRepositoryJpa.save(new Room("Sala 4", 3, defaultCinema));
        RoomRequestDTO dto = new RoomRequestDTO("Sala 4 Premium", 5, defaultCinema.getId());

        mockMvc.perform(put("/api/rooms/" + original.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Sala 4 Premium")))
                .andExpect(jsonPath("$.capacity", is(5)));
    }

    @Test
    void updateRoom_ReturnsNotFound_WhenRoomMissing() throws Exception {

        String token = testUtils.authenticateAs(UserRole.ADMIN, TicketCategory.REGULAR).get("token");

        RoomRequestDTO dto = new RoomRequestDTO("Sala X", 2, defaultCinema.getId());

        mockMvc.perform(put("/api/rooms/12345")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateRoom_ReturnsConflict_WhenDuplicateName() throws Exception {

        String token = testUtils.authenticateAs(UserRole.ADMIN, TicketCategory.REGULAR).get("token");

        roomRepositoryJpa.save(new Room("Sala VIP", 2, defaultCinema));
        Room second = roomRepositoryJpa.save(new Room("Sala Premium", 3, defaultCinema));

        RoomRequestDTO dto = new RoomRequestDTO("Sala VIP", 3, defaultCinema.getId());

        mockMvc.perform(put("/api/rooms/" + second.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }

 */
}

