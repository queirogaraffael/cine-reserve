package com.example.cinema.api.controllers;

import com.example.cinema.api.domain.services.PaymentService;
import com.example.cinema.api.shared.dtos.payment.requests.PaymentMasterDTO;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Operation(summary = "Processar pagamento")
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/purchase/{purchase_id}")
    public ResponseEntity<?> processPayment(@PathVariable("purchase_id") Long purchaseId, @RequestBody PaymentMasterDTO paymentMasterDTO) {
        var paymentResponse = paymentService.processPayment(purchaseId, paymentMasterDTO);
        return ResponseEntity.ok(paymentResponse);

    }

}
