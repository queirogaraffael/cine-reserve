package com.example.cinema.api.infrastructure.event.listener.payment;

import com.example.cinema.api.domain.payment.events.PaymentCardInitiatedEvent;
import com.example.cinema.api.application.service.EmailService;
import com.example.cinema.api.application.dto.email.PaymentCardInitiatedNotificationData;
import com.example.cinema.api.domain.user.exception.UserNotFoundException;
import com.example.cinema.api.EmailSendException;
import com.example.cinema.api.infrastructure.persistence.UserRepositoryJpa;
import com.example.cinema.api.infrastructure.persistence.projection.UserNameEmailProjection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
public class EmailPaymentCardInitiatedListener {

    private final EmailService emailService;
    private final UserRepositoryJpa userRepositoryJpa;

    public EmailPaymentCardInitiatedListener(EmailService emailService, UserRepositoryJpa userRepositoryJpa) {
        this.emailService = emailService;
        this.userRepositoryJpa = userRepositoryJpa;
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePaymentInitiated(PaymentCardInitiatedEvent event) {

        log.info("Processando notificação de pagamento com cartão iniciado: paymentId={}, userId={}", event.getIdPayment(), event.getUserId());

        try {
            UserNameEmailProjection user = userRepositoryJpa.findProjectedById(event.getUserId())
                    .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado para notificação de pagamento: userId=" + event.getUserId()
                                    + ", paymentId=" + event.getIdPayment()));

            PaymentCardInitiatedNotificationData notificationData =
                    new PaymentCardInitiatedNotificationData(user.getName(), user.getEmail(), event.getPaymentDate(), event.getTotalPrice());

            emailService.notifyPaymentCardInitiatedEmail(notificationData);

            log.info("Notificação de pagamento com cartão enviada com sucesso: paymentId={}, email={}",
                    event.getIdPayment(), user.getEmail());

        } catch (EmailSendException e) {
            log.error("Falha ao enviar e-mail de pagamento com cartão iniciado: paymentId={}, userId={}",
                    event.getIdPayment(), event.getUserId(), e);

        } catch (UserNotFoundException e) {
            log.error("Dados não encontrados durante notificação de pagamento: paymentId={}, userId={}",
                    event.getIdPayment(), event.getUserId(), e);

        } catch (Exception e) {
            log.error("Erro inesperado ao processar notificação de pagamento com cartão: paymentId={}, userId={}",
                    event.getIdPayment(), event.getUserId(), e);
        }
    }

}


