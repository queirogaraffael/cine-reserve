package com.example.cinema.api.controller;

import com.example.cinema.api.application.dto.movie.MovieExhibitionCardDTO;
import com.example.cinema.api.application.dto.movie.MovieExhibitionRequestDTO;
import com.example.cinema.api.application.dto.movie.MovieListingFilterDTO;
import com.example.cinema.api.application.service.MovieExhibitionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.example.cinema.api.infrastructure.security.AuthenticatedUser;

import java.util.List;

@Tag(name = "Exhibitions")
@RestController
public class MovieExhibitionController {

    private final MovieExhibitionService movieExhibitionService;

    public MovieExhibitionController(MovieExhibitionService movieExhibitionService) {
        this.movieExhibitionService = movieExhibitionService;
    }

    @GetMapping("/api/cinemas/{cinemaId}/exhibitions")
    public ResponseEntity<List<MovieExhibitionCardDTO>> listExhibitions(
            @PathVariable Long cinemaId,
            MovieListingFilterDTO filter
    ) {
        return ResponseEntity.ok(movieExhibitionService.listExhibitions(cinemaId, filter));
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'CINEMA_ADMIN')")
    @PostMapping("/api/exhibitions")
    public ResponseEntity<MovieExhibitionCardDTO> createExhibition(@Valid @RequestBody MovieExhibitionRequestDTO dto, @AuthenticationPrincipal AuthenticatedUser user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(movieExhibitionService.createExhibition(dto, user));
    }

    @GetMapping("/api/exhibitions/{id}")
    public ResponseEntity<MovieExhibitionCardDTO> getExhibitionById(@PathVariable Long id) {
        return ResponseEntity.ok(movieExhibitionService.getExhibitionById(id));
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'CINEMA_ADMIN')")
    @PatchMapping("/api/exhibitions/{id}/activate")
    public ResponseEntity<Void> activateExhibition(@PathVariable Long id, @AuthenticationPrincipal AuthenticatedUser user) {
        movieExhibitionService.activateExhibition(id, user);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'CINEMA_ADMIN')")
    @PatchMapping("/api/exhibitions/{id}/deactivate")
    public ResponseEntity<Void> deactivateExhibition(@PathVariable Long id, @AuthenticationPrincipal AuthenticatedUser user) {
        movieExhibitionService.deactivateExhibition(id, user);
        return ResponseEntity.noContent().build();
    }
}
