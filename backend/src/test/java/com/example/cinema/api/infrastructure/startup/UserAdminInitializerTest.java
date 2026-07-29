package com.example.cinema.api.infrastructure.startup;

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
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class UserAdminInitializerTest {

    @Mock
    private UserRepositoryJpa userRepositoryJpa;

    @Mock
    private CinemaRepositoryJpa cinemaRepositoryJpa;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserAdminInitializer userAdminInitializer;

    @Test
    void run_createsSuperAdminAndCinemaAdmin() throws Exception {
        ReflectionTestUtils.setField(userAdminInitializer, "adminEmail", "admin@teste.com");
        ReflectionTestUtils.setField(userAdminInitializer, "cinemaAdminEmail", "cinema_admin@teste.com");
        ReflectionTestUtils.setField(userAdminInitializer, "adminPassword", "123");
        ReflectionTestUtils.setField(userAdminInitializer, "cinemaAdminPassword", "123");

        Cinema cinema = new Cinema();
        ReflectionTestUtils.setField(cinema, "id", 1L);

        when(userRepositoryJpa.existsByEmail("admin@teste.com")).thenReturn(false);
        when(userRepositoryJpa.existsByEmail("cinema_admin@teste.com")).thenReturn(false);
        when(cinemaRepositoryJpa.findAll()).thenReturn(List.of(cinema));
        when(passwordEncoder.encode(any())).thenReturn("encoded");

        userAdminInitializer.run();

        verify(userRepositoryJpa, times(2)).save(any(User.class));
    }
}
