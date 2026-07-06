package com.example.cinema.api.controller;

import com.example.cinema.api.application.dto.login.UserLoginDTO;
import com.example.cinema.api.domain.user.User;
import com.example.cinema.api.domain.user.UserRole;
import com.example.cinema.api.infrastructure.persistence.UserRepositoryJpa;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("integration")
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LoginControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepositoryJpa userRepositoryJpa;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        userRepositoryJpa.deleteAll();
    }

    @Test
    void testLoginSuccess() throws Exception {
        User user = new User("123.456.789-00", "Raffael Queiroga", "raffael@example.com", passwordEncoder.encode("senha123456"), "11999999999", null, null, true, LocalDate.now(), LocalDate.of(1998, 1, 1), UserRole.USER);
        userRepositoryJpa.save(user);

        UserLoginDTO loginDTO = new UserLoginDTO("raffael@example.com", "senha123456");

        mockMvc.perform(post("/api/auth/login")
                        .header("X-Device-Id", "device-test-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    @Test
    void testLoginWrongPassword() throws Exception {
        User user = new User("123.456.789-00", "Raffael Queiroga", "raffael@example.com", passwordEncoder.encode("senha123456"), "11999999999", null, null, true, LocalDate.now(), LocalDate.of(1998, 1, 1), UserRole.USER);
        userRepositoryJpa.save(user);

        UserLoginDTO loginDTO = new UserLoginDTO("raffael@example.com", "senhaErrada");

        mockMvc.perform(post("/api/auth/login")
                        .header("X-Device-Id", "device-test-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testLoginNonExistentEmail() throws Exception {
        UserLoginDTO loginDTO = new UserLoginDTO("inexistente@example.com", "senha123456");

        mockMvc.perform(post("/api/auth/login")
                        .header("X-Device-Id", "device-test-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testLoginLockedAccount() throws Exception {
        User user = new User("123.456.789-00", "Raffael Queiroga", "raffael@example.com", passwordEncoder.encode("senha123456"), "11999999999", null, null, true, LocalDate.now(), LocalDate.of(1998, 1, 1), UserRole.USER);
        user.lockAccount(LocalDateTime.now().plusMinutes(30));
        userRepositoryJpa.save(user);

        UserLoginDTO loginDTO = new UserLoginDTO("raffael@example.com", "senha123456");

        mockMvc.perform(post("/api/auth/login")
                        .header("X-Device-Id", "device-test-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isLocked());
    }

    @Test
    void testLoginLockAfterMaxAttempts() throws Exception {
        User user = new User("123.456.789-00", "Raffael Queiroga", "raffael@example.com", passwordEncoder.encode("senha123456"), "11999999999", null, null, true, LocalDate.now(), LocalDate.of(1998, 1, 1), UserRole.USER);
        userRepositoryJpa.save(user);

        UserLoginDTO loginDTO = new UserLoginDTO("raffael@example.com", "senhaErrada");

        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post("/api/auth/login")
                            .header("X-Device-Id", "device-test-123")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginDTO)))
                    .andExpect(status().isUnauthorized());
        }

        mockMvc.perform(post("/api/auth/login")
                        .header("X-Device-Id", "device-test-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isLocked());
    }

    @Test
    void testLoginWithUppercaseEmail() throws Exception {
        User user = new User("123.456.789-00", "Raffael Queiroga", "raffael@example.com", passwordEncoder.encode("senha123456"), "11999999999", null, null, true, LocalDate.now(), LocalDate.of(1998, 1, 1), UserRole.USER);
        userRepositoryJpa.save(user);

        UserLoginDTO loginDTO = new UserLoginDTO("RAFFAEL@EXAMPLE.COM", "senha123456");

        mockMvc.perform(post("/api/auth/login")
                        .header("X-Device-Id", "device-test-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    @Test
    void testLoginDisabledUser() throws Exception {
        User user = new User("123.456.789-00", "Raffael Queiroga", "raffael@example.com", passwordEncoder.encode("senha123456"), "11999999999", null, null, true, LocalDate.now(), LocalDate.of(1998, 1, 1), UserRole.USER);
        user.desativar();
        userRepositoryJpa.save(user);

        UserLoginDTO loginDTO = new UserLoginDTO("raffael@example.com", "senha123456");

        mockMvc.perform(post("/api/auth/login")
                        .header("X-Device-Id", "device-test-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isForbidden());
    }
}
