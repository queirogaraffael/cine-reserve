package com.example.cinema.api.controllers;

import com.example.cinema.api.domain.enums.PaymentStatus;
import com.example.cinema.api.domain.services.PaymentService;
import com.example.cinema.api.shared.dtos.payment.requests.PaymentMasterDTO;
import com.example.cinema.api.shared.dtos.payment.response.PaymentGetResponseDTO;
import com.example.cinema.api.shared.dtos.payment.response.PaymentResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Operation(summary = "Processar qualquer tipo de pagamento (PIX, CARTÃO)")
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/purchases/{purchaseId}")
    public ResponseEntity<PaymentResponseDTO> processUnifiedPayment(
            @PathVariable Long purchaseId,
            @Valid @RequestBody PaymentMasterDTO paymentMasterDTO
    ) {

        PaymentResponseDTO paymentResponse = paymentService.processPayment(
                purchaseId,
                paymentMasterDTO.getPaymentDetails()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(paymentResponse);
    }

    @GetMapping("/{id}/status")
    @Operation(summary = "Consultar status do pagamento")
    public ResponseEntity<PaymentStatus> getPaymentStatus(
            @Parameter(description = "ID do pagamento", example = "42")
            @PathVariable("id") Long idPayment) {

        return ResponseEntity.ok(paymentService.getPaymentStatus(idPayment));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um pagamento pelo ID", description = "Retorna os detalhes de um pagamento específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pagamento encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pagamento não encontrado")
    })
    public ResponseEntity<PaymentGetResponseDTO> getById(@PathVariable Long id) {
        PaymentGetResponseDTO response = paymentService.getPayment(id);
        return ResponseEntity.ok(response);
    }

}
