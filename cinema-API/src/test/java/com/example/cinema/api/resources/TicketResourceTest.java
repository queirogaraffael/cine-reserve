package com.example.cinema.api.resources;


import com.example.cinema.api.domain.entities.*;
import com.example.cinema.api.domain.enums.UserCategory;
import com.example.cinema.api.domain.enums.UserRole;
import com.example.cinema.api.domain.repositories.*;
import com.example.cinema.api.shared.dtos.login.UserLoginDTO;
import com.example.cinema.api.shared.dtos.tickets.TicketRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TicketResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private MovieSessionRepository movieSessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private GenreRepository genreRepository;

    private String jwtToken;

    @BeforeEach
    void setup() {
        ticketRepository.deleteAll();
        movieSessionRepository.deleteAll();
        userRepository.deleteAll();
        movieRepository.deleteAll();
        roomRepository.deleteAll();
        genreRepository.deleteAll();
    }

    @Test
    void testCreateTicket() throws Exception {
        jwtToken = authenticateAs(UserRole.USER);

        Room room = new Room();
        room.setNumber("Sala 1");
        room.setCapacity(10);
        Room savedRoom = roomRepository.save(room);

        Genre genre = new Genre();
        genre.setName("Sci-Fi");
        Genre genreCreated = genreRepository.save(genre);

        Movie movie = new Movie();
        movie.setTitle("Inception");
        movie.setDuration(120);
        movie.setReleaseDate(LocalDate.of(2010, 7, 16));
        movie.setGenre(genreCreated);
        Movie savedMovie = movieRepository.save(movie);

        MovieSession session = new MovieSession();
        session.setCinemaRoom(savedRoom);
        session.setMovie(savedMovie);
        session.setShowDate(LocalDate.now().plusDays(1));
        session.setStartTime(LocalTime.of(20, 0));
        session.setEndTime(LocalTime.of(22, 30));
        session.setBasePrice(BigDecimal.valueOf(30.00));
        MovieSession savedSession = movieSessionRepository.save(session);

        TicketRequestDTO ticketRequestDTO = new TicketRequestDTO(5, savedSession.getId());

        mockMvc.perform(post("/api/tickets")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ticketRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.seatNumber").value(5));
    }

    @Test
    void testCreateTicket_InvalidSeatNumber() throws Exception {
        jwtToken = authenticateAs(UserRole.USER);

        Room room = new Room();
        room.setNumber("Sala Pequena");
        room.setCapacity(2);
        Room savedRoom = roomRepository.save(room);

        Genre genre = new Genre();
        genre.setName("Action");
        Genre genreCreated = genreRepository.save(genre);

        Movie movie = new Movie();
        movie.setTitle("The Matrix");
        movie.setDuration(150);
        movie.setReleaseDate(LocalDate.of(1999, 3, 31));
        movie.setGenre(genreCreated);
        Movie savedMovie = movieRepository.save(movie);

        MovieSession session = new MovieSession();
        session.setCinemaRoom(savedRoom);
        session.setMovie(savedMovie);
        session.setShowDate(LocalDate.now().plusDays(1));
        session.setStartTime(LocalTime.of(18, 0));
        session.setEndTime(LocalTime.of(20, 30));
        session.setBasePrice(BigDecimal.valueOf(25.00));
        MovieSession savedSession = movieSessionRepository.save(session);

        TicketRequestDTO ticketRequestDTO = new TicketRequestDTO(3, savedSession.getId());

        mockMvc.perform(post("/api/tickets")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ticketRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("Assento inválido"));
    }

    @Test
    void testCreateTicket_SeatAlreadyTaken() throws Exception {
        jwtToken = authenticateAs(UserRole.USER);

        Room room = new Room();
        room.setNumber("Sala 2");
        room.setCapacity(5);
        Room savedRoom = roomRepository.save(room);

        Genre genre = new Genre();
        genre.setName("Drama");
        Genre genreCreated = genreRepository.save(genre);

        Movie movie = new Movie();
        movie.setTitle("Forrest Gump");
        movie.setDuration(142);
        movie.setReleaseDate(LocalDate.of(1994, 7, 6));
        movie.setGenre(genreCreated);
        Movie savedMovie = movieRepository.save(movie);

        MovieSession session = new MovieSession();
        session.setCinemaRoom(savedRoom);
        session.setMovie(savedMovie);
        session.setShowDate(LocalDate.now().plusDays(1));
        session.setStartTime(LocalTime.of(19, 0));
        session.setEndTime(LocalTime.of(21, 30));
        session.setBasePrice(BigDecimal.valueOf(28.00));
        MovieSession savedSession = movieSessionRepository.save(session);

        // Criar um usuário para o ticket existente
        String existingTicketUsername = "existing_user_" + System.currentTimeMillis();
        User existingTicketUser = new User();
        existingTicketUser.setUsername(existingTicketUsername);
        existingTicketUser.setName("Existing Ticket User");
        existingTicketUser.setEmail(existingTicketUsername + "@test.com");
        existingTicketUser.setPassword(passwordEncoder.encode("senha123"));
        existingTicketUser.setDataJoined(LocalDate.parse("2024-01-01"));
        existingTicketUser.setBirthdate(LocalDate.parse("1990-01-01"));
        existingTicketUser.setRole(UserRole.USER);
        existingTicketUser.setCategory(UserCategory.REGULAR);
        userRepository.save(existingTicketUser);

        Ticket existingTicket = new Ticket();
        existingTicket.setSeatNumber(2);
        existingTicket.setMovieSession(savedSession);
        existingTicket.setUser(existingTicketUser);
        ticketRepository.save(existingTicket);

        TicketRequestDTO ticketRequestDTO = new TicketRequestDTO(2, savedSession.getId());

        mockMvc.perform(post("/api/tickets")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ticketRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("Assento já reservado"));
    }

    @Test
    void testCreateTicket_MovieSessionNotFound() throws Exception {
        jwtToken = authenticateAs(UserRole.ADMIN);

        TicketRequestDTO ticketRequestDTO = new TicketRequestDTO(1, 9999L);

        mockMvc.perform(post("/api/tickets")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ticketRequestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value("Sessão de filme não encontrada"));
    }

    @Test
    void testCreateTicket_Unauthorized() throws Exception {

        Room room = new Room();
        room.setNumber("Sala 3");
        room.setCapacity(8);
        Room savedRoom = roomRepository.save(room);

        Genre genre = new Genre();
        genre.setName("Comedy");
        Genre genreCreated = genreRepository.save(genre);

        Movie movie = new Movie();
        movie.setTitle("The Hangover");
        movie.setDuration(100);
        movie.setReleaseDate(LocalDate.of(2009, 6, 5));
        movie.setGenre(genreCreated);
        Movie savedMovie = movieRepository.save(movie);

        MovieSession session = new MovieSession();
        session.setCinemaRoom(savedRoom);
        session.setMovie(savedMovie);
        session.setShowDate(LocalDate.now().plusDays(1));
        session.setStartTime(LocalTime.of(21, 0));
        session.setEndTime(LocalTime.of(22, 40));
        session.setBasePrice(BigDecimal.valueOf(20.00));
        MovieSession savedSession = movieSessionRepository.save(session);

        TicketRequestDTO ticketRequestDTO = new TicketRequestDTO(1, savedSession.getId());

        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ticketRequestDTO)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testCreateTicket_AsAdmin() throws Exception {
        jwtToken = authenticateAs(UserRole.ADMIN);

        Room room = new Room();
        room.setNumber("Sala Admin");
        room.setCapacity(15);
        Room savedRoom = roomRepository.save(room);

        Genre genre = new Genre();
        genre.setName("Thriller");
        Genre genreCreated = genreRepository.save(genre);

        Movie movie = new Movie();
        movie.setTitle("Parasite");
        movie.setDuration(132);
        movie.setReleaseDate(LocalDate.of(2019, 5, 30));
        movie.setGenre(genreCreated);
        Movie savedMovie = movieRepository.save(movie);

        MovieSession session = new MovieSession();
        session.setCinemaRoom(savedRoom);
        session.setMovie(savedMovie);
        session.setShowDate(LocalDate.now().plusDays(1));
        session.setStartTime(LocalTime.of(17, 0));
        session.setEndTime(LocalTime.of(19, 12));
        session.setBasePrice(BigDecimal.valueOf(35.00));
        MovieSession savedSession = movieSessionRepository.save(session);

        TicketRequestDTO ticketRequestDTO = new TicketRequestDTO(7, savedSession.getId());

        mockMvc.perform(post("/api/tickets")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ticketRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.seatNumber").value(7));
    }


    private String authenticateAs(UserRole role) throws Exception {
        String username = "user_" + role.name().toLowerCase() + "_" + System.currentTimeMillis();

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setName("Test " + role.name());
        newUser.setEmail(username + "@test.com");
        newUser.setPassword(passwordEncoder.encode("senha123"));
        newUser.setDataJoined(LocalDate.parse("2024-01-01"));
        newUser.setBirthdate(LocalDate.parse("1990-01-01"));
        newUser.setRole(role);
        newUser.setCategory(UserCategory.REGULAR);
        userRepository.save(newUser);


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
