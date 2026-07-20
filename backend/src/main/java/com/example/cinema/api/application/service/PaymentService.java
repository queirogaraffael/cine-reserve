package com.example.cinema.api.application.service;

import com.example.cinema.api.application.dto.payment.PaymentOrderContext;
import com.example.cinema.api.application.dto.payment.PaymentUserContext;
import com.example.cinema.api.application.dto.payment.response.gateway.PaymentGatewayResult;
import com.example.cinema.api.domain.payment.Payment;
import com.example.cinema.api.domain.payment.exception.PaymentNotFoundException;
import com.example.cinema.api.domain.order.Order;
import com.example.cinema.api.domain.order.exception.OrderAlreadyHasPaymentException;
import com.example.cinema.api.domain.order.exception.OrderNotFoundException;
import com.example.cinema.api.domain.user.User;
import com.example.cinema.api.domain.payment.PaymentStatus;
import com.example.cinema.api.domain.payment.PaymentType;
import com.example.cinema.api.application.payment.context.PaymentContext;
import com.example.cinema.api.domain.payment.events.PaymentCardInitiatedEvent;
import com.example.cinema.api.infrastructure.persistence.PaymentRepositoryJpa;
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
    private final PaymentRepositoryJpa paymentRepositoryJpa;
    private final UserService userService;
    private final PaymentContext paymentContext;
    private final ApplicationEventPublisher eventPublisher;

    public PaymentService(OrderRepositoryJpa orderRepository, PaymentRepositoryJpa paymentRepositoryJpa, UserService userService, PaymentContext paymentContext, ApplicationEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.paymentRepositoryJpa = paymentRepositoryJpa;
        this.userService = userService;
        this.paymentContext = paymentContext;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public PaymentResponseDTO processPayment(Long orderId, PaymentRequestDTO paymentRequestDTO, UUID userId) {

        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new OrderNotFoundException(
                        "Pedido não encontrado ou não pertence ao usuário"));

        if (paymentRepositoryJpa.existsByOrder(order)) {
            throw new OrderAlreadyHasPaymentException("Esse pedido já tem um pagamento associado");
        }

        User user = userService.findById(userId);

        PaymentOrderContext purchaseCtx = PaymentOrderContext.from(order);
        PaymentUserContext userCtx = PaymentUserContext.from(user);

        PaymentGatewayResult gatewayResult = paymentContext.execute(purchaseCtx, userCtx, paymentRequestDTO);

        Payment payment = new Payment(order, paymentRequestDTO.getPaymentType());
        payment.registerTransaction(gatewayResult.transactionId());
        payment.updateStatus(PaymentStatus.fromValue(gatewayResult.status()), gatewayResult.statusDetail());

        Payment savedPayment = paymentRepositoryJpa.save(payment);

        if (payment.getPaymentMethod() == PaymentType.CARD) {
            eventPublisher.publishEvent(new PaymentCardInitiatedEvent(
                    savedPayment.getId(),
                    userId,
                    payment.getPaymentDate(),
                    order.getTotalPrice()
            ));
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
                .orElseThrow(() -> new PaymentNotFoundException("Pagamento para o pedido: " + orderId + " não encontrado/não disponível."));
    }
}
