package com.example.cinema.api.application.event.listener.payment;

import com.example.cinema.api.application.dto.payment.response.PaymentStatusUpdateEventDTO;
import com.example.cinema.api.application.service.PaymentSseEmitterService;
import com.example.cinema.api.domain.payment.events.PixPaymentStatusUpdatedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class PaymentSseNotificationListener {

    private final PaymentSseEmitterService paymentSseEmitterService;

    public PaymentSseNotificationListener(PaymentSseEmitterService paymentSseEmitterService) {
        this.paymentSseEmitterService = paymentSseEmitterService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPixPaymentStatusUpdated(PixPaymentStatusUpdatedEvent event) {
        
        PaymentStatusUpdateEventDTO dto = new PaymentStatusUpdateEventDTO(
                event.orderId(),
                event.status(),
                event.detail()
        );

        paymentSseEmitterService.notifyPaymentUpdate(event.orderId(), dto);
    }
}
