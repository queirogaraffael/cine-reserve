package com.example.cinema.api.infrastructure.event.listener.user;

import com.example.cinema.api.application.dto.email.UserCreatedNotificationData;
import com.example.cinema.api.application.service.EmailService;
import com.example.cinema.api.domain.user.event.UserCreatedEvent;
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
class EmailUserWelcomeListenerTest {

    @Mock
    private EmailService emailService;

    @InjectMocks
    private EmailUserWelcomeListener listener;

    @Test
    void shouldSendWelcomeAndVerificationEmailOnUserCreatedEvent() {
        UUID userId = UUID.randomUUID();
        UserCreatedEvent event = new UserCreatedEvent(userId, "Raffael Queiroga", "raffael@example.com", "123456");

        listener.handleUserCreated(event);

        ArgumentCaptor<UserCreatedNotificationData> captor = ArgumentCaptor.forClass(UserCreatedNotificationData.class);
        verify(emailService).sendWelcomeEmailComVerificacao(captor.capture());

        UserCreatedNotificationData data = captor.getValue();
        assertThat(data.getEmail()).isEqualTo("raffael@example.com");
        assertThat(data.getName()).isEqualTo("Raffael Queiroga");
        assertThat(data.getCodigoVerificacao()).isEqualTo("123456");
    }
}
