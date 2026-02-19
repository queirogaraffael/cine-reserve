package com.example.cinema.api.controller;

import com.example.cinema.api.application.service.TicketService;
import com.example.cinema.api.shared.dtos.tickets.TicketResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Tickets")
@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;
    private final CacheControl noCache;

    public TicketController(TicketService ticketService, @Qualifier("noCachePrivate") CacheControl noCache) {
        this.ticketService = ticketService;
        this.noCache = noCache;
    }

    @Operation(summary = "Buscar ticket por ID", description = "Busca um ticket pelo ID")
    @ApiResponse(responseCode = "200", description = "Ticket encontrado")
    @ApiResponse(responseCode = "404", description = "Ticket não encontrado")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/{id}")
    public ResponseEntity<TicketResponseDTO> getTicketById(@PathVariable Long id) {
        TicketResponseDTO ticket = ticketService.getById(id);

        return ResponseEntity.ok().cacheControl(noCache).body(ticket);
    }


    @Operation(summary = "Buscar tickets por ID da compra", description = "Busca todos os tickets de uma compra")
    @ApiResponse(responseCode = "200", description = "Tickets encontrados")
    @ApiResponse(responseCode = "404", description = "Tickets não encontrados")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/purchase/{purchaseId}")
    public ResponseEntity<List<TicketResponseDTO>> getTicketsByPurchaseId(@PathVariable Long purchaseId) {
        List<TicketResponseDTO> tickets = ticketService.getAllByPurchaseId(purchaseId);

        return ResponseEntity.ok().cacheControl(noCache).body(tickets);
    }

}
