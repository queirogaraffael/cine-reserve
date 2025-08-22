package com.example.cinema.api.resources;

import com.example.cinema.api.domain.entities.Genre;
import com.example.cinema.api.domain.entities.User;
import com.example.cinema.api.domain.enums.UserCategory;
import com.example.cinema.api.domain.enums.UserRole;
import com.example.cinema.api.domain.repositories.GenreRepository;
import com.example.cinema.api.domain.repositories.UserRepository;
import com.example.cinema.api.shared.dtos.genre.GenreRequestDTO;
import com.example.cinema.api.shared.dtos.genre.GenreUpdateDTO;
import com.example.cinema.api.shared.dtos.login.UserLoginDTO;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class GenreResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String jwtToken;

    @BeforeEach
    void setup() throws Exception {
        userRepository.deleteAll();
        genreRepository.deleteAll();
    }

    @Test
    void testCreateGenre() throws Exception {

        jwtToken = authenticateAs(UserRole.ADMIN);

        GenreRequestDTO genreRequestDTO = new GenreRequestDTO("Action");

        mockMvc.perform(post("/api/genres")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(genreRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Action"));
    }


    @Test
    void testFindById() throws Exception {

        Genre novoGenero = new Genre();
        novoGenero.setName("Action");

        Genre genre = genreRepository.save(novoGenero);

        mockMvc.perform(get("/api/genres/{id}", genre.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(genre.getId()))
                .andExpect(jsonPath("$.name").value("Action"));
    }

    @Test
    void testFindAllPageable() throws Exception {

        Genre generoUm = new Genre();
        generoUm.setName("Ação");
        genreRepository.save(generoUm);

        Genre generoDois = new Genre();
        generoDois.setName("Drama");
        genreRepository.save(generoDois);

        mockMvc.perform(get("/api/genres")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    void testFindByNameContainingIgnoreCase() throws Exception {

        Genre generoUm = new Genre();
        generoUm.setName("Action");
        genreRepository.save(generoUm);

        Genre generoDois = new Genre();
        generoDois.setName("Adventure");
        genreRepository.save(generoDois);

        mockMvc.perform(get("/api/genres/search")
                        .param("name", "act")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Action"));
    }


    @Test
    void testUpdateGenre() throws Exception {

        jwtToken = authenticateAs(UserRole.ADMIN);

        Genre genero = new Genre();
        genero.setName("Action");
        Genre generoSalvo = genreRepository.save(genero);

        GenreUpdateDTO genreUpdateDTO = new GenreUpdateDTO("Action Adventure");

        mockMvc.perform(put("/api/genres/{id}", generoSalvo.getId())
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(genreUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Action Adventure"));
    }

    @Test
    void testUpdateGenreConflict() throws Exception {
        jwtToken = authenticateAs(UserRole.ADMIN);

        Genre generoUm = new Genre();
        generoUm.setName("Action");
        Genre generoUmSalvo = genreRepository.save(generoUm);

        Genre generoDois = new Genre();
        generoDois.setName("Adventure");
        genreRepository.save(generoDois);

        GenreUpdateDTO genreUpdateDTO = new GenreUpdateDTO("Adventure");

        mockMvc.perform(put("/api/genres/{id}", generoUmSalvo.getId())
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(genreUpdateDTO)))
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