package com.example.cinema.api.controller;

import org.springframework.security.test.context.support.WithMockUser;
import com.example.cinema.api.domain.genre.Genre;
import com.example.cinema.api.infrastructure.persistence.GenreRepositoryJpa;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("integration")
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GenreControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GenreRepositoryJpa genreRepository;

    @BeforeEach
    void setup() throws Exception {
        genreRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateGenre() throws Exception {

        GenreRequestDTO genreRequestDTO = new GenreRequestDTO("Action");

        mockMvc.perform(post("/api/genres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(genreRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Action"));
    }


    @Test
    void testFindById() throws Exception {

        Genre genre = genreRepository.save(new Genre("Action"));

        mockMvc.perform(get("/api/genres/{id}", genre.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(genre.getId()))
                .andExpect(jsonPath("$.name").value("Action"));
    }

    @Test
    void testFindAllPageable() throws Exception {

        genreRepository.save(new Genre("Ação"));
        genreRepository.save(new Genre("Drama"));

        mockMvc.perform(get("/api/genres")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    void testFindByNameContainingIgnoreCase() throws Exception {

        genreRepository.save(new Genre("Action"));
        genreRepository.save(new Genre("Adventure"));

        mockMvc.perform(get("/api/genres/search")
                        .param("name", "act")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Action"));
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateGenre() throws Exception {

        Genre generoSalvo = genreRepository.save(new Genre("Action"));

        GenreUpdateDTO genreUpdateDTO = new GenreUpdateDTO("Action Adventure");

        mockMvc.perform(put("/api/genres/{id}", generoSalvo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(genreUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Action Adventure"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateGenreConflict() throws Exception {

        Genre generoUmSalvo = genreRepository.save(new Genre("Action"));
        genreRepository.save(new Genre("Adventure"));

        GenreUpdateDTO genreUpdateDTO = new GenreUpdateDTO("Adventure");

        mockMvc.perform(put("/api/genres/{id}", generoUmSalvo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(genreUpdateDTO)))
                .andExpect(status().isConflict());
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteGenre() throws Exception {

        Genre saved = genreRepository.save(new Genre("To Delete"));

        mockMvc.perform(delete("/api/genres/{id}", saved.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/genres/{id}", saved.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testCreateGenre_ReturnsForbidden_WhenUserIsNotAdmin() throws Exception {
        GenreRequestDTO genreRequestDTO = new GenreRequestDTO("Action");

        mockMvc.perform(post("/api/genres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(genreRequestDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDeleteGenre_ReturnsUnauthorized_WhenAnonymous() throws Exception {
        mockMvc.perform(delete("/api/genres/1"))
                .andExpect(status().isUnauthorized());
    }

}