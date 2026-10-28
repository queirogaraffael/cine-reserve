package com.example.cinema.api.application.service;

import com.example.cinema.api.application.dto.movie.MovieExhibitionRequestDTO;
import com.example.cinema.api.domain.movie.MovieFormat;
import com.example.cinema.api.domain.movie.AudioType;
import com.example.cinema.api.infrastructure.persistence.CinemaRepositoryJpa;
import com.example.cinema.api.infrastructure.persistence.MovieExhibitionRepositoryJpa;
import com.example.cinema.api.infrastructure.persistence.MovieRepositoryJpa;
import com.example.cinema.api.infrastructure.security.AuthenticatedUser;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class MovieExhibitionServiceTest {

    @Mock
    private MovieExhibitionRepositoryJpa movieExhibitionRepositoryJpa;

    @Mock
    private MovieRepositoryJpa movieRepositoryJpa;

    @Mock
    private CinemaRepositoryJpa cinemaRepositoryJpa;

    @Mock
    private CinemaAdminService cinemaAdminService;

    @InjectMocks
    private MovieExhibitionService movieExhibitionService;

    @Test
    void createExhibition_asCinemaAdmin_wrongCinema_throwsAccessDenied() {
        AuthenticatedUser user = new AuthenticatedUser(UUID.randomUUID(),
                List.of(new SimpleGrantedAuthority("ROLE_CINEMA_ADMIN")), 2L);
        MovieExhibitionRequestDTO request = new MovieExhibitionRequestDTO(1L, 1L, MovieFormat.F2D, AudioType.DUBLADO);

        org.mockito.Mockito
                .doThrow(new AccessDeniedException("Você não tem permissão para acessar ou modificar este cinema."))
                .when(cinemaAdminService).validateCinemaOwnership(user, 1L);

        assertThrows(AccessDeniedException.class, () -> movieExhibitionService.createExhibition(request, user));
    }
}
