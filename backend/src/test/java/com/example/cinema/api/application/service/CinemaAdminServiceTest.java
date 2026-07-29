package com.example.cinema.api.application.service;

import com.example.cinema.api.application.dto.user.UserRequestDTO;
import org.springframework.test.util.ReflectionTestUtils;
import com.example.cinema.api.domain.cinema.Cinema;
import com.example.cinema.api.domain.user.User;
import com.example.cinema.api.infrastructure.persistence.CinemaRepositoryJpa;
import com.example.cinema.api.infrastructure.persistence.UserRepositoryJpa;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class CinemaAdminServiceTest {

    @Mock
    private UserRepositoryJpa userRepositoryJpa;

    @Mock
    private CinemaRepositoryJpa cinemaRepositoryJpa;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CinemaAdminService cinemaAdminService;

    @Test
    void createCinemaAdmin_success() {
        Cinema cinema = new Cinema();
        ReflectionTestUtils.setField(cinema, "id", 1L);

        UserRequestDTO request = new UserRequestDTO("Admin", "admin@teste.com", "12345678", "11999999999");

        when(cinemaRepositoryJpa.findById(1L)).thenReturn(Optional.of(cinema));
        when(userRepositoryJpa.existsByEmail("admin@teste.com")).thenReturn(false);
        when(passwordEncoder.encode("12345678")).thenReturn("encoded");

        cinemaAdminService.createCinemaAdmin(1L, request);

        verify(userRepositoryJpa, times(1)).save(any(User.class));
    }
}
