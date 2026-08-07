package com.example.cinema.api.application.event.listener.order;

import com.example.cinema.api.domain.order.Order;
import com.example.cinema.api.domain.order.OrderStatus;
import com.example.cinema.api.domain.order.exception.OrderNotFoundException;
import com.example.cinema.api.domain.payment.events.PaymentConfirmedEvent;
import com.example.cinema.api.infrastructure.persistence.OrderRepositoryJpa;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class OrderPaymentListener {

    private final OrderRepositoryJpa orderRepository;

    public OrderPaymentListener(OrderRepositoryJpa orderRepository) {
        this.orderRepository = orderRepository;
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onPaymentApproved(PaymentConfirmedEvent event) {
        Order order = orderRepository.findById(event.orderId())
                .orElseThrow(() -> new OrderNotFoundException("Pedido não encontrado para o evento de pagamento: " + event.orderId()));
        
        order.moveToStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);
    }
}
