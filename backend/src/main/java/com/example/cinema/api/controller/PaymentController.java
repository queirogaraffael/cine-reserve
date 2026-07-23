package com.example.cinema.api.controller;

import com.example.cinema.api.domain.payment.PaymentStatus;
import com.example.cinema.api.application.service.PaymentService;
import com.example.cinema.api.application.dto.payment.requests.PaymentMasterDTO;
import com.example.cinema.api.application.dto.payment.response.PaymentGetResponseDTO;
import com.example.cinema.api.application.dto.payment.response.PaymentResponseDTO;
import com.example.cinema.api.infrastructure.security.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/payments")
@Tag(name = "Payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final CacheControl noCache;

    public PaymentController(PaymentService paymentService, @Qualifier("noCachePrivate") CacheControl noCache) {
        this.paymentService = paymentService;
        this.noCache = noCache;
    }

    @Operation(summary = "Processar pagamento (PIX ou CARTÃO)", description = """
        Processa o pagamento de um pedido utilizando o método informado.

        Métodos suportados:

        • PIX \s
        Gera o QR Code e o código copia-e-cola para pagamento instantâneo.

        • CARTÃO \s
        Processa o pagamento utilizando um token de cartão previamente gerado.

        O campo `paymentMethod` determina qual estrutura deve ser enviada em `paymentDetails`.

        ---

        Exemplos

        Requisição PIX:

        ```json
        {
          "paymentMethod": "PIX",
          "paymentDetails": {}
        }
        ```

        Requisição CARTÃO:

        ```json
        {
          "paymentMethod": "CARD",
          "paymentDetails": {
            "card_token": "tok_123456",
            "payment_method_id": "visa",
            "installments": 3
          }
        }
        ```

        ---
       \s
        Após o processamento, a resposta retornará os dados específicos do método de pagamento utilizado.
       \s"""
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pagamento criado com sucesso", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Dados de pagamento inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado", content = @Content)})
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/orders/{orderId}")
    public ResponseEntity<PaymentResponseDTO> processUnifiedPayment(
            @Parameter(description = "ID do pedido que será pago", example = "123") @PathVariable Long orderId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Objeto de pagamento unificado. A estrutura de `paymentDetails` depende do campo `paymentMethod`.", required = true)
            @Valid @RequestBody PaymentMasterDTO paymentMasterDTO,

            @AuthenticationPrincipal AuthenticatedUser principal) {

        PaymentResponseDTO paymentResponse = paymentService.processPayment(orderId, paymentMasterDTO.getPaymentDetails(), principal.getId());

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(paymentResponse.getPaymentId()).toUri();

        return ResponseEntity.created(uri).body(paymentResponse);
    }


    @Operation(summary = "Busca um pagamento pelo ID", description = "Retorna os detalhes de um pagamento específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pagamento encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pagamento não encontrado")})
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/{id}")
    public ResponseEntity<PaymentGetResponseDTO> getById(@PathVariable Long id) {

        PaymentGetResponseDTO response = paymentService.getPayment(id);

        return ResponseEntity.ok()
                .cacheControl(noCache)
                .body(response);
    }

    @Operation(summary = "Buscar pagamento por ID do pedido", description = "Busca o pagamento associado a um pedido específico do usuário autenticado")
    @ApiResponse(responseCode = "200", description = "Pagamento encontrado")
    @ApiResponse(responseCode = "404", description = "Pagamento não encontrado")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/by-order/{orderId}")
    public ResponseEntity<PaymentGetResponseDTO> getPaymentByOrderId(
            @PathVariable Long orderId,
            @AuthenticationPrincipal AuthenticatedUser principal) {

        PaymentGetResponseDTO responseDTO = paymentService.getPaymentByOrderId(orderId, principal.getId());

        return ResponseEntity.ok()
                .cacheControl(noCache)
                .body(responseDTO);
    }

}
