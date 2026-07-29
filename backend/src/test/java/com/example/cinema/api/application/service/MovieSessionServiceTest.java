package com.example.cinema.api.application.service;

import com.example.cinema.api.application.dto.movieSession.MovieSessionRequestDTO;
import com.example.cinema.api.domain.cinema.Cinema;
import com.example.cinema.api.domain.movie.MovieExhibition;
import com.example.cinema.api.infrastructure.persistence.MovieExhibitionRepositoryJpa;
import com.example.cinema.api.infrastructure.persistence.MovieSessionRepositoryJpa;
import com.example.cinema.api.infrastructure.security.AuthenticatedUser;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.test.util.ReflectionTestUtils;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class MovieSessionServiceTest {

    @Mock
    private MovieExhibitionRepositoryJpa movieExhibitionRepositoryJpa;

    @Mock
    private MovieSessionRepositoryJpa movieSessionRepositoryJpa;

    @Mock
    private CinemaAdminService cinemaAdminService;

    @InjectMocks
    private MovieSessionService movieSessionService;

    @Test
    void createSession_asCinemaAdmin_wrongCinema_throwsAccessDenied() {
        AuthenticatedUser user = new AuthenticatedUser(UUID.randomUUID(), List.of(new SimpleGrantedAuthority("ROLE_CINEMA_ADMIN")), 2L);
        MovieSessionRequestDTO request = new MovieSessionRequestDTO(LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(12, 0), BigDecimal.TEN, 1L, 1L);
        
        Cinema cinema = new Cinema();
        ReflectionTestUtils.setField(cinema, "id", 1L);
        MovieExhibition exhibition = new MovieExhibition();
        ReflectionTestUtils.setField(exhibition, "cinema", cinema);
        
        when(movieSessionRepositoryJpa.existsSessionConflict(any(), any(), any(), any())).thenReturn(false);
        when(movieExhibitionRepositoryJpa.findById(1L)).thenReturn(Optional.of(exhibition));
        
        org.mockito.Mockito.doThrow(new AccessDeniedException("Você não tem permissão para acessar ou modificar este cinema."))
            .when(cinemaAdminService).validateCinemaOwnership(user, 1L);
        
        assertThrows(AccessDeniedException.class, () -> movieSessionService.createSession(request, user));
    }
}
