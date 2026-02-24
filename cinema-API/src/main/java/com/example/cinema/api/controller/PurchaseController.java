package com.example.cinema.api.controller;

import com.example.cinema.api.application.dto.purchase.PurchaseResponseDTO;
import com.example.cinema.api.application.service.PurchaseService;
import com.example.cinema.api.application.dto.purchase.TicketPurchaseRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Tag(name = "Purchases")
@RestController
@RequestMapping("/api/purchases")
public class PurchaseController {

    private final PurchaseService purchaseService;

    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @Operation(
            summary = "Criar nova compra",
            description = "Cria uma nova compra. O header X-Idempotency-Key é obrigatório para evitar duplicidade em caso de falhas de rede.",
            parameters = {
                    @Parameter(
                            name = "X-Idempotency-Key",
                            description = "Chave única para garantir a idempotência da requisição (ex: UUID)",
                            required = true,
                            in = ParameterIn.HEADER,
                            schema = @Schema(type = "string", format = "uuid")
                    )
            }
    )
    @ApiResponse(responseCode = "201", description = "Purchase criada com sucesso.")
    @ApiResponse(responseCode = "400", description = "Erro de validação ou chave de idempotência ausente")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping
    public ResponseEntity<PurchaseResponseDTO> createPurchase(
            @RequestHeader("X-Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody TicketPurchaseRequestDTO ticketPurchaseRequestDTO) {

        PurchaseResponseDTO purchase = purchaseService.createPurchase(ticketPurchaseRequestDTO, idempotencyKey);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(purchase.getId()).toUri();

        return ResponseEntity.created(uri).body(purchase);
    }

    @Operation(summary = "Atualizar idempotency key da compra")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "Bearer Authentication")
    @PatchMapping("/{id}/idempotency-key")
    public ResponseEntity<Void> atualizarIdempotencyKey(
            @Parameter(description = "ID da compra", example = "10")
            @PathVariable("id") Long idPurchase,

            @Parameter(description = "Nova idempotency key", example = "abc-123-xyz")
            @RequestParam String idempotencyKey) {

        purchaseService.modificarIdempotencyKeyPurchase(idPurchase, idempotencyKey);
        return ResponseEntity.noContent().build();
    }

}
