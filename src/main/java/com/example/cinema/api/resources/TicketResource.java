package com.example.cinema.api.resources;

import com.example.cinema.api.shared.dtos.tickets.TicketRequestDTO;
import com.example.cinema.api.shared.dtos.tickets.TicketResponseDTO;
import com.example.cinema.api.domain.services.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Tickets")
@RestController
@RequestMapping("/api/tickets")
@EnableMethodSecurity
public class TicketResource {

    private final TicketService ticketService;

    public TicketResource(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @Operation(summary = "Criar novo ingresso", description = "Cria um novo ingresso")
    @ApiResponse(responseCode = "201", description = "Ingresso criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Erro de validação")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping
    public ResponseEntity<TicketResponseDTO> createTicket(@RequestBody TicketRequestDTO ticketDTO) {
        TicketResponseDTO createdTicket = ticketService.criarTickt(ticketDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTicket);
    }


}
