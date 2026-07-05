package com.example.cinema.api.infrastructure.event.listener.confirmacao;

import com.example.cinema.api.application.dto.email.EmailVerificacaoNotificationData;
import com.example.cinema.api.application.service.EmailService;
import com.example.cinema.api.domain.confirmacao.event.EmailVerificacaoEvent;
import com.example.cinema.api.infrastructure.email.exception.EmailSendException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
public class EmailVerificacaoListener {

    private final EmailService emailServicePort;

    public EmailVerificacaoListener(EmailService emailServicePort) {
        this.emailServicePort = emailServicePort;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleEmailVerificacao(EmailVerificacaoEvent event) {
        try {
            EmailVerificacaoNotificationData notificationData = new EmailVerificacaoNotificationData(
                    event.getNome(),
                    event.getEmail(),
                    event.getCodigo()
            );

            emailServicePort.sendEmailVerificacao(notificationData);
        } catch (EmailSendException e) {
            log.error("Falha ao enviar e-mail de verificação: usuarioId={}", event.getUsuarioId(), e);
        } catch (Exception e) {
            log.error("Erro inesperado ao processar notificação de verificação: usuarioId={}", event.getUsuarioId(), e);
        }
    }
}
