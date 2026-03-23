package com.example.cinema.api.controller;

import com.example.cinema.api.application.dto.purchase.PurchaseIdempotencyResponseDTO;
import com.example.cinema.api.application.dto.purchase.PurchaseResponseDTO;
import com.example.cinema.api.application.dto.purchase.UpdateIdempotencyKeyRequestDTO;
import com.example.cinema.api.application.service.PurchaseService;
import com.example.cinema.api.application.dto.purchase.TicketPurchaseRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
                            schema = @Schema(type = "string", format = "uuid"))}
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

    @Operation(
            summary = "Atualizar idempotency key de uma compra",
            description = """
                Permite atualizar a idempotency key de uma compra existente.

                Regras:
                - A compra deve pertencer ao usuário autenticado.
                - Não é permitido atualizar se já existir pagamento associado.
                - A idempotency key deve ser única.

                Uso comum:
                - Recuperação de operações interrompidas
                - Garantia de idempotência em pagamentos
                """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Idempotency key atualizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Compra não encontrada"),
            @ApiResponse(responseCode = "409", description = "Compra já possui pagamento associado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado")})
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "Bearer Authentication")
    @PatchMapping("/{id}/idempotency-key")
    public ResponseEntity<PurchaseIdempotencyResponseDTO> atualizarIdempotencyKey(

            @Parameter(description = "ID da compra", example = "10", required = true)
            @PathVariable("id")
            Long idPurchase,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Nova idempotency key", required = true)
            @Valid
            @RequestBody
            UpdateIdempotencyKeyRequestDTO request) {

        PurchaseIdempotencyResponseDTO response = purchaseService.modificarIdempotencyKeyPurchase(idPurchase, request.getIdempotencyKey());

        return ResponseEntity.ok(response);
    }

}
