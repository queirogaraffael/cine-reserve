package com.example.cinema.api.domain.payment.events.listeners;

import com.example.cinema.api.domain.payment.events.PaymentCardInitiatedEvent;
import com.example.cinema.api.domain.services.EmailService;
import com.example.cinema.api.shared.exceptions.EmailSendException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
public class EmailPaymentCardInitiatedListener {

    private final EmailService emailService;

    public EmailPaymentCardInitiatedListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePaymentInitiated(PaymentCardInitiatedEvent event) {

        try {
            emailService.notifyPaymentCardInitiated(event.getUser(), event.getPurchase(), event.getPayment());
        } catch (EmailSendException e) {
            log.error("Falha ao enviar email de Pagamento de Cartão iniciado {} para {}", event.getPayment().getId(), event.getUser().getEmail(), e);
        }

    }

}


