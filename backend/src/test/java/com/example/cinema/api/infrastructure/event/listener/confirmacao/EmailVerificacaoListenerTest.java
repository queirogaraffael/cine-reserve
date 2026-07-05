package com.example.cinema.api.infrastructure.event.listener.confirmacao;

import com.example.cinema.api.application.dto.email.EmailVerificacaoNotificationData;
import com.example.cinema.api.application.service.EmailService;
import com.example.cinema.api.domain.confirmacao.event.EmailVerificacaoEvent;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class EmailVerificacaoListenerTest {

    @Mock
    private EmailService emailService;

    @InjectMocks
    private EmailVerificacaoListener listener;

    @Test
    void shouldSendVerificationEmailOnEvent() {
        UUID userId = UUID.randomUUID();
        EmailVerificacaoEvent event = new EmailVerificacaoEvent(userId, "raffael@example.com", "Raffael Queiroga", "654321");

        listener.handleEmailVerificacao(event);

        ArgumentCaptor<EmailVerificacaoNotificationData> captor = ArgumentCaptor.forClass(EmailVerificacaoNotificationData.class);
        verify(emailService).sendEmailVerificacao(captor.capture());

        EmailVerificacaoNotificationData data = captor.getValue();
        assertThat(data.getEmail()).isEqualTo("raffael@example.com");
        assertThat(data.getName()).isEqualTo("Raffael Queiroga");
        assertThat(data.getCodigo()).isEqualTo("654321");
    }
}
