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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Exhibitions")
@RestController
public class MovieExhibitionController {

    private final MovieExhibitionService movieExhibitionService;

    public MovieExhibitionController(MovieExhibitionService movieExhibitionService) {
        this.movieExhibitionService = movieExhibitionService;
    }

    @GetMapping({"/api/cinemas/{cinemaId}/exibicoes", "/api/cinemas/{cinemaId}/exhibitions"})
    public ResponseEntity<List<MovieExhibitionCardDTO>> listExhibitions(
            @PathVariable Long cinemaId,
            MovieListingFilterDTO filter
    ) {
        return ResponseEntity.ok(movieExhibitionService.listExhibitions(cinemaId, filter));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping({"/api/exibicoes", "/api/exhibitions"})
    public ResponseEntity<MovieExhibitionCardDTO> createExhibition(@Valid @RequestBody MovieExhibitionRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(movieExhibitionService.createExhibition(dto));
    }

    @GetMapping({"/api/exibicoes/{id}", "/api/exhibitions/{id}"})
    public ResponseEntity<MovieExhibitionCardDTO> getExhibitionById(@PathVariable Long id) {
        return ResponseEntity.ok(movieExhibitionService.getExhibitionById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping({"/api/exibicoes/{id}/activate", "/api/exhibitions/{id}/activate"})
    public ResponseEntity<Void> activateExhibition(@PathVariable Long id) {
        movieExhibitionService.activateExhibition(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping({"/api/exibicoes/{id}/deactivate", "/api/exhibitions/{id}/deactivate"})
    public ResponseEntity<Void> deactivateExhibition(@PathVariable Long id) {
        movieExhibitionService.deactivateExhibition(id);
        return ResponseEntity.noContent().build();
    }
}
