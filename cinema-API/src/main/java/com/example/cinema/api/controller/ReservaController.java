package com.example.cinema.api.controller;

import com.example.cinema.api.domain.service.ReservaService;
import com.example.cinema.api.shared.dtos.seatreservation.SeatReservationRequestDTO;
import com.example.cinema.api.shared.dtos.seatreservation.SeatReservationResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/session/{sessionId}")
    public ResponseEntity<SeatReservationResponseDTO> criarReserva(
            @PathVariable Long sessionId,
            @RequestBody SeatReservationRequestDTO requestDTO
    ) {
        SeatReservationResponseDTO response =
                reservaService.criarReserva(sessionId, requestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

