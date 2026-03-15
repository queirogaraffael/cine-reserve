package com.example.cinema.api.infrastructure.event.listener.purchase;

import com.example.cinema.api.domain.user.exception.UserNotFoundException;
import com.example.cinema.api.infrastructure.persistence.projection.UserNameEmailProjection;
import com.example.cinema.api.application.dto.email.PurchaseCreatedNotificationData;
import com.example.cinema.api.domain.purchase.event.PurchaseCreatedEvent;
import com.example.cinema.api.application.service.EmailService;
import com.example.cinema.api.EmailSendException;
import com.example.cinema.api.infrastructure.persistence.UserRepositoryJpa;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
public class EmailPurchaseNotificationListener {

    private final EmailService emailServicePort;
    private final UserRepositoryJpa userRepositoryJpa;

    public EmailPurchaseNotificationListener(EmailService emailServicePort, UserRepositoryJpa userRepositoryJpa) {
        this.emailServicePort = emailServicePort;
        this.userRepositoryJpa = userRepositoryJpa;
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePurchase(PurchaseCreatedEvent event) {

        log.info("Processando notificação de compra: purchaseId={}, userId={}", event.getPurchaseId(), event.getUserId());

        try {
            UserNameEmailProjection user = userRepositoryJpa.findProjectedById(event.getUserId())
                    .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado para notificação da compra: userId=" + event.getUserId()
                                    + ", purchaseId=" + event.getPurchaseId()));

            PurchaseCreatedNotificationData notificationData = new PurchaseCreatedNotificationData(user.getName(), user.getEmail(),
                    event.getPurchaseDate(), event.getTotalPrice());

            emailServicePort.notifyPurchaseCreatedEmail(notificationData);

            log.info("Notificação de compra enviada com sucesso: purchaseId={}, email={}", event.getPurchaseId(), user.getEmail());

        } catch (EmailSendException e) {
            log.error("Falha ao enviar e-mail da compra: purchaseId={}, userId={}", event.getPurchaseId(), event.getUserId(), e);

        } catch (UserNotFoundException e) {
            log.error("Dados não encontrados durante a notificação da compra: purchaseId={}, userId={}", event.getPurchaseId(), event.getUserId(), e);

        } catch (Exception e) {
            log.error("Erro inesperado ao processar notificação de compra: purchaseId={}, userId={}", event.getPurchaseId(), event.getUserId(), e);
        }
    }

}
