package com.example.cinema.api.application.service;

import com.example.cinema.api.domain.payment.Payment;
import com.example.cinema.api.domain.purchase.Purchase;
import com.example.cinema.api.domain.purchase.exception.PurchaseAlreadyHasPaymentException;
import com.example.cinema.api.domain.user.User;
import com.example.cinema.api.domain.payment.PaymentStatus;
import com.example.cinema.api.domain.payment.PaymentType;
import com.example.cinema.api.domain.payment.context.PaymentContext;
import com.example.cinema.api.domain.payment.events.PaymentCardInitiatedEvent;
import com.example.cinema.api.infrastructure.persistence.PaymentRepositoryJpa;
import com.example.cinema.api.infrastructure.persistence.PurchaseRepositoryJpa;
import com.example.cinema.api.application.dto.payment.requests.PaymentRequestDTO;
import com.example.cinema.api.application.dto.payment.response.PaymentGetResponseDTO;
import com.example.cinema.api.application.dto.payment.response.PaymentResponseDTO;
import com.example.cinema.api.shared.exception.ResourceNotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

    private final PurchaseRepositoryJpa purchaseRepository;
    private final PaymentRepositoryJpa paymentRepositoryJpa;
    private final UserService userService;
    private final PaymentContext paymentContext;
    private final ApplicationEventPublisher eventPublisher;

    public PaymentService(PurchaseRepositoryJpa purchaseRepository, PaymentRepositoryJpa paymentRepositoryJpa, UserService userService, PaymentContext paymentContext, ApplicationEventPublisher eventPublisher) {
        this.purchaseRepository = purchaseRepository;
        this.paymentRepositoryJpa = paymentRepositoryJpa;
        this.userService = userService;
        this.paymentContext = paymentContext;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public PaymentResponseDTO processPayment(Long purchaseId, PaymentRequestDTO paymentRequestDTO) {
        User user = userService.getAuthenticatedUser();

        Purchase purchase = purchaseRepository.findByIdAndUser(purchaseId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Compra não encontrada ou não pertence ao usuário"));

        if (paymentRepositoryJpa.existsByPurchase(purchase)) {
            throw new PurchaseAlreadyHasPaymentException("Essa compra já tem um pagamento associado");
        }

        Payment payment = new Payment(purchase, paymentRequestDTO.getPaymentType());

        PaymentResponseDTO response = paymentContext.execute(purchase, user, paymentRequestDTO, payment);

        Payment savedPayment = paymentRepositoryJpa.save(payment);

        response.setPaymentId(savedPayment.getId());

        // evento só é disparado para cartão, pois o pagamento é iniciado imediatamente (o que não acontece no PIX)
        if (payment.getPaymentMethod() == PaymentType.CARD) {
            eventPublisher.publishEvent(new PaymentCardInitiatedEvent(payment.getId(), user.getId(), payment.getPaymentDate(), purchase.getTotalPrice()));
        }

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

    @Transactional(readOnly = true)
    public PaymentGetResponseDTO getPaymentByPurchaseId(Long purchaseId){
        User user = userService.getAuthenticatedUser();

        return paymentRepositoryJpa.findPaymentDtoByPurchaseIdAndUser(purchaseId, user).orElseThrow(()-> new ResourceNotFoundException("Pagamento para a compra: " + " não encontrado/não disponível."));
    }

}
