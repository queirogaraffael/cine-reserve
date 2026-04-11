package com.example.cinema.api.controller;

import com.example.cinema.api.shared.TestUtils;
import com.example.cinema.api.domain.genre.Genre;
import com.example.cinema.api.domain.ticket.TicketCategory;
import com.example.cinema.api.domain.user.UserRole;
import com.example.cinema.api.infrastructure.persistence.GenreRepositoryJpa;
import com.example.cinema.api.infrastructure.persistence.UserRepositoryJpa;
import com.example.cinema.api.application.dto.genre.GenreRequestDTO;
import com.example.cinema.api.application.dto.genre.GenreUpdateDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("integration")
@SpringBootTest
@AutoConfigureMockMvc
class GenreControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GenreRepositoryJpa genreRepository;

    @Autowired
    private UserRepositoryJpa userRepositoryJpa;

    @Autowired
    private TestUtils testUtils;


    @BeforeEach
    void setup() throws Exception {
        userRepositoryJpa.deleteAll();
        genreRepository.deleteAll();
    }

    @Test
    void testCreateGenre() throws Exception {

        String token = testUtils.authenticateAs(UserRole.ADMIN, TicketCategory.REGULAR).get("token");

        GenreRequestDTO genreRequestDTO = new GenreRequestDTO("Action");

        mockMvc.perform(post("/api/genres")
                        .header("Authorization", "Bearer " + token)
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

        String token = testUtils.authenticateAs(UserRole.ADMIN, TicketCategory.REGULAR).get("token");

        Genre genero = new Genre();
        genero.setName("Action");
        Genre generoSalvo = genreRepository.save(genero);

        GenreUpdateDTO genreUpdateDTO = new GenreUpdateDTO("Action Adventure");

        mockMvc.perform(put("/api/genres/{id}", generoSalvo.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(genreUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Action Adventure"));
    }

    @Test
    void testUpdateGenreConflict() throws Exception {

        String token = testUtils.authenticateAs(UserRole.ADMIN, TicketCategory.REGULAR).get("token");

        Genre generoUm = new Genre();
        generoUm.setName("Action");
        Genre generoUmSalvo = genreRepository.save(generoUm);

        Genre generoDois = new Genre();
        generoDois.setName("Adventure");
        genreRepository.save(generoDois);

        GenreUpdateDTO genreUpdateDTO = new GenreUpdateDTO("Adventure");

        mockMvc.perform(put("/api/genres/{id}", generoUmSalvo.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(genreUpdateDTO)))
                .andExpect(status().isConflict());
    }
}