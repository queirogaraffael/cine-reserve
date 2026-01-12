package com.example.cinema.api.domain.services;

import com.example.cinema.api.domain.entities.Payment;
import com.example.cinema.api.domain.entities.Purchase;
import com.example.cinema.api.domain.entities.User;
import com.example.cinema.api.domain.enums.PaymentStatus;
import com.example.cinema.api.domain.payment.context.PaymentContext;
import com.example.cinema.api.infrastructure.repositories.PaymentRepository;
import com.example.cinema.api.infrastructure.repositories.PurchaseRepository;
import com.example.cinema.api.shared.dtos.payment.requests.PaymentRequestDTO;
import com.example.cinema.api.shared.dtos.payment.response.gateway.PaymentGatewayResponseDTO;
import com.example.cinema.api.shared.dtos.payment.response.PaymentGetResponseDTO;
import com.example.cinema.api.shared.dtos.payment.response.PaymentResponseDTO;
import com.example.cinema.api.shared.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PaymentService {

    private final PurchaseRepository purchaseRepository;
    private final PaymentRepository paymentRepository;
    private final UserService userService;
    private final PaymentContext paymentContext;

    public PaymentService(
            PurchaseRepository purchaseRepository,
            PaymentRepository paymentRepository,
            UserService userService,
            PaymentContext paymentContext
    ) {
        this.purchaseRepository = purchaseRepository;
        this.paymentRepository = paymentRepository;
        this.userService = userService;
        this.paymentContext = paymentContext;
    }

    @Transactional
    public PaymentResponseDTO processPayment(Long purchaseId, PaymentRequestDTO paymentRequestDTO) {
        User user = userService.getAuthenticatedUser();
        Purchase purchase = purchaseRepository.findById(purchaseId).orElseThrow();

        Payment payment = new Payment();
        payment.setPurchase(purchase);
        payment.setPaymentMethod(paymentRequestDTO.getPaymentType());
        payment.setPaymentDate(LocalDateTime.now());

        PaymentResponseDTO response = paymentContext.execute(purchase, user, paymentRequestDTO, payment);

        Payment savedPayment = paymentRepository.save(payment);

        response.setPaymentId(savedPayment.getId());

        return response;
    }


    @Transactional(readOnly = true)
    public PaymentStatus getPaymentStatus(Long idPayment){
        return paymentRepository.findStatusById(idPayment).orElseThrow(()-> new ResourceNotFoundException("Payment " + idPayment + " não encontrado."));
    }

    @Transactional(readOnly = true)
    public PaymentGetResponseDTO getPayment(Long idPayment) {
        return paymentRepository.findPaymentById(idPayment)
                .orElseThrow(() -> new ResourceNotFoundException("Pagamento com ID " + idPayment + " não encontrado."));
    }
}
