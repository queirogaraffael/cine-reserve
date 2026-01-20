package com.example.cinema.api.domain.services;

import com.example.cinema.api.domain.entities.Payment;
import com.example.cinema.api.domain.entities.Purchase;
import com.example.cinema.api.domain.entities.User;
import com.example.cinema.api.domain.enums.PaymentStatus;
import com.example.cinema.api.domain.payment.context.PaymentContext;
import com.example.cinema.api.infrastructure.persistence.PaymentRepositoryJpa;
import com.example.cinema.api.infrastructure.persistence.PurchaseRepositoryJpa;
import com.example.cinema.api.shared.dtos.payment.requests.PaymentRequestDTO;
import com.example.cinema.api.shared.dtos.payment.response.PaymentGetResponseDTO;
import com.example.cinema.api.shared.dtos.payment.response.PaymentResponseDTO;
import com.example.cinema.api.shared.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PaymentService {

    private final PurchaseRepositoryJpa purchaseRepository;
    private final PaymentRepositoryJpa paymentRepositoryJpa;
    private final UserService userService;
    private final PaymentContext paymentContext;

    public PaymentService(
            PurchaseRepositoryJpa purchaseRepository,
            PaymentRepositoryJpa paymentRepositoryJpa,
            UserService userService,
            PaymentContext paymentContext
    ) {
        this.purchaseRepository = purchaseRepository;
        this.paymentRepositoryJpa = paymentRepositoryJpa;
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

        Payment savedPayment = paymentRepositoryJpa.save(payment);

        response.setPaymentId(savedPayment.getId());

        return response;
    }


    @Transactional(readOnly = true)
    public PaymentStatus getPaymentStatus(Long idPayment){
        return paymentRepositoryJpa.findStatusById(idPayment).orElseThrow(()-> new ResourceNotFoundException("Payment " + idPayment + " não encontrado."));
    }

    @Transactional(readOnly = true)
    public PaymentGetResponseDTO getPayment(Long idPayment) {
        return paymentRepositoryJpa.findPaymentById(idPayment)
                .orElseThrow(() -> new ResourceNotFoundException("Pagamento com ID " + idPayment + " não encontrado."));
    }
}
