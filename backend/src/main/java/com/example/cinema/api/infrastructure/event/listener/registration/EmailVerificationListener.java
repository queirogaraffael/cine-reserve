package com.example.cinema.api.infrastructure.event.listener.registration;

import com.example.cinema.api.application.dto.email.EmailVerificationNotificationData;
import com.example.cinema.api.application.service.EmailService;
import com.example.cinema.api.domain.registration.event.EmailVerificationEvent;
import com.example.cinema.api.infrastructure.email.exception.EmailSendException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
public class EmailVerificationListener {

    private final EmailService emailService;

    public EmailVerificationListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleEmailVerification(EmailVerificationEvent event) {
        try {
            EmailVerificationNotificationData notificationData = new EmailVerificationNotificationData(
                    event.getName(),
                    event.getEmail(),
                    event.getCode()
            );

            emailService.sendEmailVerificacao(notificationData);
        } catch (EmailSendException e) {
            log.error("Falha ao enviar e-mail de verificação: userId={}", event.getUserId(), e);
        } catch (Exception e) {
            log.error("Erro inesperado ao processar notificação de verificação: userId={}", event.getUserId(), e);
        }
    }
}