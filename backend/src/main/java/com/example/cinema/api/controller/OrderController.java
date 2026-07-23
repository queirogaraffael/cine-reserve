package com.example.cinema.api.controller;

import com.example.cinema.api.application.dto.order.CreateOrderRequestDTO;
import com.example.cinema.api.application.dto.order.OrderResponseDTO;
import com.example.cinema.api.application.dto.order.OrderSeatSelectionRequestDTO;
import com.example.cinema.api.application.service.OrderService;
import com.example.cinema.api.infrastructure.security.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Tag(name = "Orders")
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(
            summary = "Criar pedido (Etapa 1)",
            description = "Cria um pedido com os tipos de ingresso e quantidades selecionados. Retorna o pedido no status CREATED."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pedido criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "404", description = "Sessão ou tipo de ingresso não encontrado"),
            @ApiResponse(responseCode = "409", description = "Sessão não disponível para compra")
    })
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(
            @Valid @RequestBody CreateOrderRequestDTO dto,
            @AuthenticationPrincipal AuthenticatedUser principal) {

        OrderResponseDTO response = orderService.createOrder(dto, principal.getId());

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @Operation(
            summary = "Selecionar assentos (Etapa 3)",
            description = "Reserva temporariamente os assentos selecionados para o pedido. Retorna o pedido no status WAITING_PAYMENT com TTL de 10 minutos."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Assentos reservados com sucesso"),
            @ApiResponse(responseCode = "400", description = "Quantidade de assentos inválida"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "404", description = "Pedido ou assento não encontrado"),
            @ApiResponse(responseCode = "409", description = "Assento já reservado por outro usuário")
    })
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/{id}/seats")
    public ResponseEntity<OrderResponseDTO> selectSeats(
            @Parameter(description = "ID do pedido", example = "5891") @PathVariable Long id,
            @Valid @RequestBody OrderSeatSelectionRequestDTO dto,
            @AuthenticationPrincipal AuthenticatedUser principal) {

        OrderResponseDTO response = orderService.selectSeats(id, dto, principal.getId());

        return ResponseEntity.ok(response);
    }
}
