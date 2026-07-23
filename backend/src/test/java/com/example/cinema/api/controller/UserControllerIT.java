package com.example.cinema.api.controller;

import com.example.cinema.api.application.dto.user.ConfirmarEmailDTO;
import com.example.cinema.api.application.dto.user.UserAddressUpdateDTO;
import com.example.cinema.api.application.dto.user.UserProfileUpdateDTO;
import com.example.cinema.api.application.dto.user.UserRequestDTO;
import com.example.cinema.api.application.service.UserService;
import com.example.cinema.api.domain.registration.RegistrationConfirmation;
import com.example.cinema.api.domain.user.Sexo;
import com.example.cinema.api.domain.user.User;
import com.example.cinema.api.domain.user.UserRole;
import com.example.cinema.api.infrastructure.persistence.RegistrationConfirmationRepositoryJpa;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("integration")
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepositoryJpa userRepositoryJpa;

    @Autowired
    private RegistrationConfirmationRepositoryJpa confirmacaoRepositoryJpa;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @MockBean
    private JavaMailSender javaMailSender;

    @BeforeEach
    void setup() {
        confirmacaoRepositoryJpa.deleteAll();
        userRepositoryJpa.deleteAll();
    }

    @Test
    void testRegisterSuccess() throws Exception {
        UserRequestDTO request = new UserRequestDTO("Raffael Queiroga", "raffael@example.com", "senha123456", "11999999999");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Raffael Queiroga"))
                .andExpect(jsonPath("$.email").value("raffael@example.com"))
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    @Test
    void testRegisterWithoutName() throws Exception {
        UserRequestDTO request = new UserRequestDTO(null, "raffael@example.com", "senha123456", "11999999999");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegisterWithoutEmail() throws Exception {
        UserRequestDTO request = new UserRequestDTO("Raffael Queiroga", null, "senha123456", "11999999999");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegisterWithInvalidEmail() throws Exception {
        UserRequestDTO request = new UserRequestDTO("Raffael Queiroga", "raffael.example.com", "senha123456", "11999999999");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegisterWithShortPassword() throws Exception {
        UserRequestDTO request = new UserRequestDTO("Raffael Queiroga", "raffael@example.com", "1234567", "11999999999");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegisterWithoutCelular() throws Exception {
        UserRequestDTO request = new UserRequestDTO("Raffael Queiroga", "raffael@example.com", "senha123456", null);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegisterDuplicateEmail() throws Exception {
        User existingUser = new User("123.456.789-00", "Existing User", "raffael@example.com", passwordEncoder.encode("senha123456"), "11988888888", null, null, false, LocalDate.now(), LocalDate.of(1990, 1, 1), UserRole.USER);
        userRepositoryJpa.save(existingUser);

        UserRequestDTO request = new UserRequestDTO("Raffael Queiroga", "raffael@example.com", "senha123456", "11999999999");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void testConfirmarEmailSuccess() throws Exception {
        var created = userService.createUser(new UserRequestDTO("Raffael Queiroga", "raffael@example.com", "senha123456", "11999999999"), "dev-1", "agent-1", "127.0.0.1");
        RegistrationConfirmation confirmacao = confirmacaoRepositoryJpa.findByUserIdAndUsedFalse(created.getId()).orElseThrow();

        ConfirmarEmailDTO dto = new ConfirmarEmailDTO(confirmacao.getCode());

        mockMvc.perform(post("/api/users/me/email-verification")
                        .header("Authorization", "Bearer " + created.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        User user = userRepositoryJpa.findById(created.getId()).orElseThrow();
        assertTrue(user.isEmailConfirmado());
    }

    @Test
    void testConfirmarEmailExpired() throws Exception {
        var created = userService.createUser(new UserRequestDTO("Raffael Queiroga", "raffael@example.com", "senha123456", "11999999999"), "dev-1", "agent-1", "127.0.0.1");
        confirmacaoRepositoryJpa.deleteByUserId(created.getId());
        confirmacaoRepositoryJpa.save(new RegistrationConfirmation(created.getId(), "123456", LocalDateTime.now().minusMinutes(20)));

        ConfirmarEmailDTO dto = new ConfirmarEmailDTO("123456");

        mockMvc.perform(post("/api/users/me/email-verification")
                        .header("Authorization", "Bearer " + created.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testConfirmarEmailAlreadyUsed() throws Exception {
        var created = userService.createUser(new UserRequestDTO("Raffael Queiroga", "raffael@example.com", "senha123456", "11999999999"), "dev-1", "agent-1", "127.0.0.1");
        RegistrationConfirmation confirmacao = confirmacaoRepositoryJpa.findByUserIdAndUsedFalse(created.getId()).orElseThrow();
        confirmacao.markAsUsed();
        confirmacaoRepositoryJpa.save(confirmacao);

        ConfirmarEmailDTO dto = new ConfirmarEmailDTO(confirmacao.getCode());

        mockMvc.perform(post("/api/users/me/email-verification")
                        .header("Authorization", "Bearer " + created.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testConfirmarEmailWrongCode() throws Exception {
        var created = userService.createUser(new UserRequestDTO("Raffael Queiroga", "raffael@example.com", "senha123456", "11999999999"), "dev-1", "agent-1", "127.0.0.1");

        ConfirmarEmailDTO dto = new ConfirmarEmailDTO("000000");

        mockMvc.perform(post("/api/users/me/email-verification")
                        .header("Authorization", "Bearer " + created.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testConfirmarEmailWithoutToken() throws Exception {
        ConfirmarEmailDTO dto = new ConfirmarEmailDTO("123456");

        mockMvc.perform(post("/api/users/me/email-verification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testUpdateProfileSuccess() throws Exception {
        var created = userService.createUser(new UserRequestDTO("Raffael Queiroga", "raffael@example.com", "senha123456", "11999999999"), "dev-1", "agent-1", "127.0.0.1");
        UserProfileUpdateDTO dto = new UserProfileUpdateDTO(Sexo.MASCULINO, LocalDate.of(1995, 5, 10), "12345678909");

        mockMvc.perform(patch("/api/users/me")
                        .header("Authorization", "Bearer " + created.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sexo").value("MASCULINO"));
    }

    @Test
    void testUpdateProfileSubset() throws Exception {
        var created = userService.createUser(new UserRequestDTO("Raffael Queiroga", "raffael@example.com", "senha123456", "11999999999"), "dev-1", "agent-1", "127.0.0.1");
        UserProfileUpdateDTO dto = new UserProfileUpdateDTO(Sexo.FEMININO, null, null);

        mockMvc.perform(patch("/api/users/me")
                        .header("Authorization", "Bearer " + created.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sexo").value("FEMININO"));
    }

    @Test
    void testUpdateProfileWithoutToken() throws Exception {
        UserProfileUpdateDTO dto = new UserProfileUpdateDTO(Sexo.MASCULINO, LocalDate.of(1995, 5, 10), "12345678909");

        mockMvc.perform(patch("/api/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testUpdateAddressSuccess() throws Exception {
        var created = userService.createUser(new UserRequestDTO("Raffael Queiroga", "raffael@example.com", "senha123456", "11999999999"), "dev-1", "agent-1", "127.0.0.1");
        UserAddressUpdateDTO dto = new UserAddressUpdateDTO("01001-000", "Praça da Sé", "100", "Apto 10", "Sé", "São Paulo", "SP");

        mockMvc.perform(patch("/api/users/me/address")
                        .header("Authorization", "Bearer " + created.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.endereco.cidade").value("São Paulo"))
                .andExpect(jsonPath("$.endereco.estado").value("SP"));
    }

    @Test
    void testUpdateAddressWithoutToken() throws Exception {
        UserAddressUpdateDTO dto = new UserAddressUpdateDTO("01001-000", "Praça da Sé", "100", "Apto 10", "Sé", "São Paulo", "SP");

        mockMvc.perform(patch("/api/users/me/address")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetProfileSuccess() throws Exception {
        var created = userService.createUser(new UserRequestDTO("Raffael Queiroga", "raffael@example.com", "senha123456", "11999999999"), "dev-1", "agent-1", "127.0.0.1");

        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer " + created.getAccessToken()))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id").value(created.getId().toString()))
                        .andExpect(jsonPath("$.email").value("raffael@example.com"))
                        .andExpect(jsonPath("$.emailConfirmado").value(false))
                        .andExpect(jsonPath("$.username").doesNotExist());
    }

    @Test
    void testGetProfileWithoutToken() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testRegisterWithUppercaseEmailNormalized() throws Exception {
        UserRequestDTO request = new UserRequestDTO("Raffael Queiroga", "RAFFAEL.MAIUSCULO@EXAMPLE.COM", "senha123456", "11999999999");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("raffael.maiusculo@example.com"));
    }

    @Test
    void testUpdateProfileUnderageUser() throws Exception {
        var created = userService.createUser(new UserRequestDTO("Raffael Queiroga", "raffael@example.com", "senha123456", "11999999999"), "dev-1", "agent-1", "127.0.0.1");
        UserProfileUpdateDTO dto = new UserProfileUpdateDTO(Sexo.MASCULINO, LocalDate.now().minusYears(16), "12345678909");

        mockMvc.perform(patch("/api/users/me")
                        .header("Authorization", "Bearer " + created.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}
