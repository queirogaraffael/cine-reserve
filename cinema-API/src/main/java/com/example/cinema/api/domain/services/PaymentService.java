package com.example.cinema.api.domain.services;

import com.example.cinema.api.domain.entities.Payment;
import com.example.cinema.api.domain.entities.Purchase;
import com.example.cinema.api.domain.entities.User;
import com.example.cinema.api.domain.enums.PaymentStatus;
import com.example.cinema.api.domain.payment.context.PaymentContext;
import com.example.cinema.api.infrastructure.repositories.PaymentRepository;
import com.example.cinema.api.infrastructure.repositories.PurchaseRepository;
import com.example.cinema.api.shared.dtos.payment.requests.PaymentMasterDTO;
import com.example.cinema.api.shared.dtos.payment.response.PaymentResponseDTO;
import com.example.cinema.api.shared.exceptions.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PaymentService {

    private final PurchaseRepository purchaseRepository;
    private final PaymentRepository paymentRepository;
    private final UserService userService;
    private final PaymentContext paymentContext;

    public PaymentService(PurchaseRepository purchaseRepository, PaymentRepository paymentRepository, UserService userService, PaymentContext paymentContext) {
        this.purchaseRepository = purchaseRepository;
        this.paymentRepository = paymentRepository;
        this.userService = userService;
        this.paymentContext = paymentContext;
    }

    @Transactional
    public PaymentResponseDTO processPayment(Long purchaseId, PaymentMasterDTO paymentMasterDTO, String idempotencyKey) {
        User user = userService.getAuthenticatedUser();

        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase not found"));

        PaymentResponseDTO response = paymentContext.executeStrategy(
                purchase,
                user,
                paymentMasterDTO,
                idempotencyKey
        );

        Payment payment = new Payment();

        payment.setPaymentMethod(paymentMasterDTO.getPaymentMethod());
        payment.setPurchase(purchase);

        payment.setTransactionId(response.getTransactionId());
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setPaymentDate(LocalDateTime.now());

        paymentRepository.save(payment);

        return response;
    }

}