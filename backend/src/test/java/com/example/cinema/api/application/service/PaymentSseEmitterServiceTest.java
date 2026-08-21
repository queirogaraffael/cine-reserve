package com.example.cinema.api.application.service;

import com.example.cinema.api.application.dto.payment.response.PaymentStatusUpdateEventDTO;
import com.example.cinema.api.domain.payment.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class PaymentSseEmitterServiceTest {

    private PaymentSseEmitterService emitterService;

    @BeforeEach
    void setUp() {
        emitterService = new PaymentSseEmitterService(600_000L);
    }

    @Test
    void subscribe_ReturnsEmitter() {
        SseEmitter emitter = emitterService.subscribe(1L);
        assertNotNull(emitter);
    }

    @Test
    void notifyPaymentUpdate_DoesNotThrow_WhenNoSubscriber() {
        PaymentStatusUpdateEventDTO update = new PaymentStatusUpdateEventDTO(1L, PaymentStatus.APPROVED, "Pagamento aprovado");
        emitterService.notifyPaymentUpdate(1L, update);
    }
}

