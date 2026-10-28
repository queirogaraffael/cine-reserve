package com.example.cinema.api.application.service;

import com.example.cinema.api.application.dto.room.RoomRequestDTO;
import com.example.cinema.api.application.dto.room.RoomResponseDTO;
import com.example.cinema.api.infrastructure.persistence.CinemaRepositoryJpa;
import com.example.cinema.api.infrastructure.security.AuthenticatedUser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock
    private CinemaRepositoryJpa cinemaRepositoryJpa;

    @Mock
    private com.example.cinema.api.infrastructure.persistence.RoomRepositoryJpa roomRepositoryJpa;

    @Mock
    private CinemaAdminService cinemaAdminService;

    @InjectMocks
    private RoomService roomService;

    @Test
    void createRoom_asCinemaAdmin_wrongCinema_throwsAccessDenied() {
        AuthenticatedUser user = new AuthenticatedUser(UUID.randomUUID(),
                List.of(new SimpleGrantedAuthority("ROLE_CINEMA_ADMIN")), 2L);
        RoomRequestDTO request = new RoomRequestDTO();
        request.setName("Sala 1");
        request.setCinemaId(1L);
        org.mockito.Mockito
                .doThrow(new AccessDeniedException("Você não tem permissão para acessar ou modificar este cinema."))
                .when(cinemaAdminService).validateCinemaOwnership(user, 1L);

        assertThrows(AccessDeniedException.class, () -> roomService.createRoom(request, user));
    }

    @Test
    void getAllRooms_superAdmin_returnsAllRooms() {
        AuthenticatedUser superAdmin = new AuthenticatedUser(
                UUID.randomUUID(),
                List.of(new SimpleGrantedAuthority("ROLE_SUPER_ADMIN")),
                null);

        Page<RoomResponseDTO> expectedPage = new PageImpl<>(List.of(
                new RoomResponseDTO(1L, "Sala A", 1L),
                new RoomResponseDTO(2L, "Sala B", 2L)));

        Pageable pageable = PageRequest.of(0, 10);
        when(roomRepositoryJpa.findAllPaginado(org.mockito.ArgumentMatchers.any(Pageable.class)))
                .thenReturn(expectedPage);

        Page<com.example.cinema.api.application.dto.room.RoomResponseDTO> result = roomService.getAllRooms(0, 10,
                superAdmin);

        Assertions.assertEquals(2, result.getTotalElements());
        Mockito.verify(roomRepositoryJpa).findAllPaginado(org.mockito.ArgumentMatchers.any(Pageable.class));
    }

    @Test
    void getAllRooms_cinemaAdmin_returnsOnlyOwnCinemaRooms() {
        Long cinemaId = 3L;
        AuthenticatedUser cinemaAdmin = new AuthenticatedUser(
                UUID.randomUUID(),
                List.of(new SimpleGrantedAuthority("ROLE_CINEMA_ADMIN")),
                cinemaId);

        Page<RoomResponseDTO> expectedPage = new PageImpl<>(List.of(
                new RoomResponseDTO(10L, "Sala X", cinemaId)));

        when(roomRepositoryJpa.findAllByCinemaIdPaginado(org.mockito.ArgumentMatchers.eq(cinemaId),
                org.mockito.ArgumentMatchers.any(Pageable.class))).thenReturn(expectedPage);

        Page<RoomResponseDTO> result = roomService.getAllRooms(0, 10, cinemaAdmin);

        Assertions.assertEquals(1, result.getTotalElements());
        Assertions.assertEquals(cinemaId, result.getContent().get(0).getCinemaId());
        Mockito.verify(roomRepositoryJpa).findAllByCinemaIdPaginado(org.mockito.ArgumentMatchers.eq(cinemaId),
                org.mockito.ArgumentMatchers.any(org.springframework.data.domain.Pageable.class));
    }
}
