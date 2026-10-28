package com.example.cinema.api.controller;

import com.example.cinema.api.application.dto.user.CinemaAdminResponseDTO;
import com.example.cinema.api.application.dto.user.UserCreatedResponseDTO;
import com.example.cinema.api.application.dto.user.UserRequestDTO;
import com.example.cinema.api.application.dto.user.UserResponseDTO;
import com.example.cinema.api.application.service.CinemaAdminService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/cinemas/{cinemaId}/admins")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class CinemaAdminController {

    private final CinemaAdminService cinemaAdminService;

    public CinemaAdminController(CinemaAdminService cinemaAdminService) {
        this.cinemaAdminService = cinemaAdminService;
    }

    @PostMapping
    public ResponseEntity<UserCreatedResponseDTO> createCinemaAdmin(
            @PathVariable Long cinemaId,
            @RequestBody @Valid UserRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cinemaAdminService.createCinemaAdmin(cinemaId, request));
    }

    @GetMapping
    public ResponseEntity<Page<CinemaAdminResponseDTO>> listCinemaAdmins(
            @PathVariable Long cinemaId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(cinemaAdminService.listCinemaAdmins(cinemaId, pageable));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> removeCinemaAdmin(
            @PathVariable Long cinemaId,
            @PathVariable UUID userId) {
        cinemaAdminService.removeCinemaAdmin(cinemaId, userId);
        return ResponseEntity.noContent().build();
    }
}
