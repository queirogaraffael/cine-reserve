package com.example.cinema.api.controllers;

import com.example.cinema.api.domain.services.PaymentService;
import com.example.cinema.api.shared.dtos.payment.requests.PaymentMasterDTO;
import com.example.cinema.api.shared.dtos.payment.requests.PaymentRequestDTO;
import com.example.cinema.api.shared.dtos.payment.response.PaymentResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
            @Valid @RequestBody PaymentMasterDTO paymentMasterDTO,
            @RequestHeader("Idempotency-Key") String idempotencyKey
    ) {

        PaymentResponseDTO paymentResponse = paymentService.processPayment(
                purchaseId,
                paymentMasterDTO.getPaymentDetails(),
                idempotencyKey
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(paymentResponse);
    }
}
