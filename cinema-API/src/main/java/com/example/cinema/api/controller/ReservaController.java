package com.example.cinema.api.controller;

import com.example.cinema.api.application.service.ReservaService;
import com.example.cinema.api.application.dto.seatreservation.SeatReservationRequestDTO;
import com.example.cinema.api.application.dto.seatreservation.SeatReservationResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/reservations")
@Tag(name = "Reservas de Assento")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @Operation(
            summary = "Criar reserva de assento",
            description = "Reserva um assento para uma sessão de filme por tempo limitado"
    )
    @ApiResponse(responseCode = "201", description = "Reserva criada com sucesso")
    @ApiResponse(responseCode = "400", description = "Assento inválido ou já reservado")
    @ApiResponse(responseCode = "404", description = "Sessão não encontrada")
    @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/session/{sessionId}")
    public ResponseEntity<SeatReservationResponseDTO> criarReserva(@PathVariable Long sessionId, @RequestBody SeatReservationRequestDTO requestDTO) {
        SeatReservationResponseDTO response = reservaService.criarReserva(sessionId, requestDTO);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(response.getId()).toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @Operation(summary = "Cancelar reserva de assento", description = "Cancela uma reserva existente do usuário, desde que ainda esteja como RESERVED")
    @ApiResponse(responseCode = "204", description = "Reserva cancelada com sucesso")
    @ApiResponse(responseCode = "404", description = "Reserva não encontrada ou não pode ser cancelada")
    @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Void> cancelarReserva(@PathVariable Long reservationId) {

        reservaService.cancelarReservaDeUsuario(reservationId);

        return ResponseEntity.noContent().build();
    }

}

