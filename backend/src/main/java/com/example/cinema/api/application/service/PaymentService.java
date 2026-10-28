package com.example.cinema.api.application.service;

import com.example.cinema.api.application.dto.payment.OrderPaymentContext;
import com.example.cinema.api.application.dto.payment.PaymentUserContext;

import com.example.cinema.api.application.dto.payment.response.gateway.PaymentGatewayResult;
import com.example.cinema.api.domain.payment.OrderPayment;
import com.example.cinema.api.domain.payment.exception.PaymentNotFoundException;
import com.example.cinema.api.domain.order.Order;
import com.example.cinema.api.domain.order.exception.OrderNotFoundException;
import com.example.cinema.api.domain.payment.exception.OrderAlreadyPaidException;
import com.example.cinema.api.domain.payment.exception.PaymentAlreadyInProgressException;
import com.example.cinema.api.domain.user.User;
import com.example.cinema.api.domain.payment.PaymentStatus;
import com.example.cinema.api.domain.payment.PaymentType;
import java.time.LocalDateTime;
import com.example.cinema.api.application.payment.context.PaymentContext;
import com.example.cinema.api.domain.payment.events.PaymentCardInitiatedEvent;
import com.example.cinema.api.infrastructure.persistence.OrderPaymentRepositoryJpa;
import com.example.cinema.api.infrastructure.persistence.OrderRepositoryJpa;
import com.example.cinema.api.application.dto.payment.requests.PaymentRequestDTO;
import com.example.cinema.api.application.dto.payment.response.PaymentGetResponseDTO;
import com.example.cinema.api.application.dto.payment.response.PaymentResponseDTO;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PaymentService {

    private final OrderRepositoryJpa orderRepository;
    private final OrderPaymentRepositoryJpa paymentRepositoryJpa;
    private final UserService userService;
    private final PaymentContext paymentContext;
    private final ApplicationEventPublisher eventPublisher;
    private final PaymentValidationService paymentValidationService;

    public PaymentService(OrderRepositoryJpa orderRepository, OrderPaymentRepositoryJpa paymentRepositoryJpa,
            UserService userService, PaymentContext paymentContext, ApplicationEventPublisher eventPublisher,
            PaymentValidationService paymentValidationService) {
        this.orderRepository = orderRepository;
        this.paymentRepositoryJpa = paymentRepositoryJpa;
        this.userService = userService;
        this.paymentContext = paymentContext;
        this.eventPublisher = eventPublisher;
        this.paymentValidationService = paymentValidationService;
    }

    @Transactional
    public PaymentResponseDTO processPayment(Long orderId, PaymentRequestDTO paymentRequestDTO, UUID userId,
            String idempotencyKey) {

        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new OrderNotFoundException(
                        "Pedido não encontrado ou não pertence ao usuário"));

        paymentRepositoryJpa.findFirstByOrderIdOrderByPaymentDateDesc(orderId).ifPresent(p -> {
            if (p.getPaymentStatus() == PaymentStatus.APPROVED) {
                throw new OrderAlreadyPaidException("Este pedido já possui um pagamento aprovado.");
            }
            if (p.getPaymentStatus() == PaymentStatus.PENDING
                    || p.getPaymentStatus() == PaymentStatus.IN_PROCESS
                    || p.getPaymentStatus() == PaymentStatus.AUTHORIZED) {
                throw new PaymentAlreadyInProgressException("Já existe um pagamento em andamento para este pedido.");
            }
        });

        order.getMovieSession().validateAvailabilityForPayment();

        User user = userService.findById(userId);

        return processPaymentInternal(order, user, paymentRequestDTO, idempotencyKey);
    }

    @Transactional
    public PaymentResponseDTO retryPayment(Long orderId, PaymentRequestDTO requestDTO, UUID userId,
            String idempotencyKey) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new OrderNotFoundException("Pedido não encontrado ou não pertence ao usuário"));

        OrderPayment latestPayment = paymentRepositoryJpa.findFirstByOrderIdOrderByPaymentDateDesc(orderId)
                .orElse(null);
        long totalAttempts = paymentRepositoryJpa.countByOrderId(orderId);

        paymentValidationService.validateRetry(order, latestPayment, totalAttempts);

        User user = userService.findById(userId);

        return processPaymentInternal(order, user, requestDTO, idempotencyKey);
    }

    private PaymentResponseDTO processPaymentInternal(Order order, User user, PaymentRequestDTO paymentRequestDTO,
            String idempotencyKey) {
        OrderPayment payment = new OrderPayment(order, paymentRequestDTO.getPaymentType(), idempotencyKey);
        OrderPayment savedPayment = paymentRepositoryJpa.save(payment);

        OrderPaymentContext purchaseCtx = OrderPaymentContext.from(order, savedPayment);
        PaymentUserContext userCtx = PaymentUserContext.from(user);

        PaymentGatewayResult gatewayResult;
        try {
            gatewayResult = paymentContext.execute(purchaseCtx, userCtx, paymentRequestDTO);
        } catch (Exception e) {
            savedPayment.registerTransaction(PaymentStatus.FAILED, "GATEWAY_ERROR", e.getMessage(),
                    LocalDateTime.now());
            paymentRepositoryJpa.save(savedPayment);
            throw e;
        }

        savedPayment.setProviderPaymentId(gatewayResult.providerPaymentId());
        savedPayment.registerTransaction(PaymentStatus.fromValue(gatewayResult.status()), "GATEWAY_RESPONSE",
                gatewayResult.statusDetail(), LocalDateTime.now());

        paymentRepositoryJpa.save(savedPayment);

        if (savedPayment.getPaymentMethod() == PaymentType.CARD) {
            eventPublisher.publishEvent(new PaymentCardInitiatedEvent(
                    savedPayment.getId(),
                    user.getId(),
                    savedPayment.getPaymentDate(),
                    order.getTotalPrice().getAmount()));
        }

        return gatewayResult.toResponseDTO(savedPayment.getId());
    }

    @Transactional(readOnly = true)
    public PaymentStatus getPaymentStatus(Long idPayment) {
        return paymentRepositoryJpa.findStatusById(idPayment)
                .orElseThrow(() -> new PaymentNotFoundException("Payment " + idPayment + " não encontrado."));
    }

    @Transactional(readOnly = true)
    public PaymentGetResponseDTO getPayment(Long idPayment) {
        return paymentRepositoryJpa.findPaymentById(idPayment)
                .orElseThrow(() -> new PaymentNotFoundException("Pagamento com ID " + idPayment + " não encontrado."));
    }

    @Transactional(readOnly = true)
    public PaymentGetResponseDTO getPaymentByOrderId(Long orderId, UUID userId) {
        return paymentRepositoryJpa.findPaymentDtoByOrderIdAndUserId(orderId, userId)
                .orElseThrow(() -> new PaymentNotFoundException(
                        "Pagamento para o pedido: " + orderId + " não encontrado/não disponível."));
    }

    @Transactional(readOnly = true)
    public void validateUserOwnsOrder(Long orderId, UUID userId) {
        if (!orderRepository.existsByIdAndUserId(orderId, userId)) {
            throw new OrderNotFoundException("Pedido não encontrado ou não pertence ao usuário");
        }
    }
}
