package com.example.cinema.api.infrastructure.event.listener;

import com.example.cinema.api.application.service.EmailService;
import com.example.cinema.api.domain.user.event.UserCreatedEvent;
import com.example.cinema.api.application.dto.email.UserCreatedNotificationData;
import com.example.cinema.api.infrastructure.exception.EmailSendException;
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

        log.info("Processando notificação de boas-vindas para novo usuário: userId={}, email={}",
                event.getUserId(), event.getEmail());

        try {
            UserCreatedNotificationData notificationData = new UserCreatedNotificationData(event.getName(), event.getEmail());

            emailServicePort.sendWelcomeEmail(notificationData);

            log.info("E-mail de boas-vindas enviado com sucesso: userId={}, email={}", event.getUserId(), event.getEmail());

        } catch (EmailSendException e) {

            log.error("Falha ao enviar e-mail de boas-vindas: userId={}, email={}", event.getUserId(), event.getEmail(), e);

        } catch (Exception e) {

            log.error("Erro inesperado ao processar notificação de criação de usuário: userId={}, email={}",
                    event.getUserId(), event.getEmail(), e);
        }
    }

}
