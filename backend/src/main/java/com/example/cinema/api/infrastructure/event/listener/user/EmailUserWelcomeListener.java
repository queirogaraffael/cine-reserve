package com.example.cinema.api.infrastructure.event.listener.user;

import com.example.cinema.api.application.service.EmailService;
import com.example.cinema.api.domain.user.event.UserCreatedEvent;
import com.example.cinema.api.application.dto.email.UserCreatedNotificationData;
import com.example.cinema.api.infrastructure.email.exception.EmailSendException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
public class EmailUserWelcomeListener {

    private final EmailService emailServicePort;

    public EmailUserWelcomeListener(EmailService emailServicePort) {
        this.emailServicePort = emailServicePort;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserCreated(UserCreatedEvent event) {
        try {
            UserCreatedNotificationData notificationData = new UserCreatedNotificationData(
                    event.getName(),
                    event.getEmail(),
                    event.getCodigoVerificacao()
            );

            emailServicePort.sendWelcomeEmailComVerificacao(notificationData);
        } catch (EmailSendException e) {
            log.error("Falha ao enviar e-mail de boas-vindas: userId={}", event.getUserId(), e);
        } catch (Exception e) {
            log.error("Erro inesperado ao processar notificação de criação de usuário: userId={}", event.getUserId(), e);
        }
    }
}
