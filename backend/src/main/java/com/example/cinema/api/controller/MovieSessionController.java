package com.example.cinema.api.controller;

import com.example.cinema.api.application.dto.movieSession.ExhibitionSessionsResponseDTO;
import com.example.cinema.api.application.dto.movieSession.MovieSessionRequestDTO;
import com.example.cinema.api.application.dto.movieSession.MovieSessionResponseDTO;
import com.example.cinema.api.application.service.MovieSessionService;
import com.example.cinema.api.infrastructure.security.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

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

    @Operation(summary = "Buscar sessão por ID", description = "Retorna os detalhes de uma sessão específica pelo seu ID")
    @ApiResponse(responseCode = "200", description = "Sessão encontrada com sucesso")
    @ApiResponse(responseCode = "404", description = "Sessão não encontrada")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/{sessionId}")
    public ResponseEntity<MovieSessionResponseDTO> getMovieSessionById(@PathVariable Long sessionId) {
        MovieSessionResponseDTO response = movieSessionService.getMovieSessionById(sessionId);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Listar assentos da sessão", description = "Retorna o mapa de assentos para uma sessão específica")
    @ApiResponse(responseCode = "200", description = "Lista de assentos retornada com sucesso")
    @ApiResponse(responseCode = "404", description = "Sessão não encontrada")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/{sessionId}/seats")
    public ResponseEntity<List<com.example.cinema.api.application.dto.seat.SeatDTO>> getAvailableSeats(@PathVariable Long sessionId) {

        List<com.example.cinema.api.application.dto.seat.SeatDTO> availableSeats = movieSessionService.getAvailableSeats(sessionId);

        return ResponseEntity.ok(availableSeats);
    }



    @Operation(summary = "Listar sessões de uma exibição",
            description = "Retorna hoje + próximos 6 dias de sessões agrupadas por data e sala com status de disponibilidade")
    @ApiResponse(responseCode = "200", description = "Sessões retornadas com sucesso")
    @ApiResponse(responseCode = "404", description = "Exibição não encontrada")
    @GetMapping
    public ResponseEntity<ExhibitionSessionsResponseDTO> getSessionsByExhibition(
            @RequestParam Long exhibitionId
    ) {
        return ResponseEntity.ok(movieSessionService.getSessionsByExhibition(exhibitionId));
    }
    
    @Operation(summary = "Listar tipos de ingresso da sessão", description = "Retorna os tipos de ingresso disponíveis com os preços já calculados considerando promoções")
    @ApiResponse(responseCode = "200", description = "Lista de ingressos retornada com sucesso")
    @ApiResponse(responseCode = "404", description = "Sessão não encontrada")
    @GetMapping("/{sessionId}/ticket-types")
    public ResponseEntity<List<com.example.cinema.api.application.dto.ticket.TicketTypeDTO>> getTicketTypes(@PathVariable Long sessionId) {
        return ResponseEntity.ok(movieSessionService.getTicketTypesWithPromotions(sessionId));
    }
}
