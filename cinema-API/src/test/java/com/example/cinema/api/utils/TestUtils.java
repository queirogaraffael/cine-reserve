package com.example.cinema.api.utils;

import com.example.cinema.api.domain.entities.User;
import com.example.cinema.api.domain.enums.UserCategory;
import com.example.cinema.api.domain.enums.UserRole;
import com.example.cinema.api.infrastructure.repositories.UserRepository;
import com.example.cinema.api.shared.dtos.login.UserLoginDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Component
public class TestUtils {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Map<String, String> authenticateAs(UserRole role, UserCategory userCategory) throws Exception {
        String username = "user_" + role.name().toLowerCase() + "_" + UUID.randomUUID().toString().substring(0, 8);

        User user = new User();
        user.setUsername(username);
        user.setName("Test " + role.name());
        user.setEmail(username + "@test.com");
        user.setPassword(passwordEncoder.encode("senha123"));
        user.setDataJoined(LocalDate.parse("2024-01-01"));
        user.setBirthdate(LocalDate.parse("1990-01-01"));
        user.setRole(role);
        user.setCategory(userCategory);

        userRepository.save(user);

        var loginDTO = new UserLoginDTO(username, "senha123");

        var result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(response);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("token", jsonNode.get("token").asText());
        tokens.put("refreshToken", jsonNode.get("refreshToken").asText());

        return tokens;
    }

}
