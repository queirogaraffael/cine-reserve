package com.example.cinema.api.controller;

import com.example.cinema.api.application.dto.movieSession.MovieSessionRequestDTO;
import com.example.cinema.api.application.dto.movieSession.MovieSessionResponseDTO;
import com.example.cinema.api.application.service.MovieSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@Tag(name = "Movie Sessions")
@RequestMapping("/api/sessions")
public class MovieSessionController {

    private final MovieSessionService movieSessionService;

    public MovieSessionController(MovieSessionService movieSessionService) {
        this.movieSessionService = movieSessionService;
    }

    @Operation(summary = "Criar nova sessão de filme", description = "Cria uma nova sessão de filme")
    @ApiResponse(responseCode = "201", description = "Sessão criada com sucesso")
    @ApiResponse(responseCode = "400", description = "Erro de validação")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor | Erro em alguma validação de negócio")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping
    public ResponseEntity<MovieSessionResponseDTO> createMovieSession(@RequestBody @Valid MovieSessionRequestDTO dto) {
        MovieSessionResponseDTO movieSessionResponseDTO = movieSessionService.createSession(dto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(movieSessionResponseDTO.getId()).toUri();

        return ResponseEntity.created(uri).body(movieSessionResponseDTO);
    }

}
