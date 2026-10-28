package com.example.cinema.api.application.service;

import com.example.cinema.api.application.dto.payment.response.PaymentStatusUpdateEventDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class PaymentSseEmitterService {

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final ExecutorService asyncExecutor = Executors.newCachedThreadPool();
    private final long sseTimeoutMs;

    public PaymentSseEmitterService(@Value("${cinereserve.sse.timeout-ms:600000}") long sseTimeoutMs) {
        this.sseTimeoutMs = sseTimeoutMs;
    }

    public SseEmitter subscribe(Long purchaseId) {
        SseEmitter emitter = new SseEmitter(sseTimeoutMs);
        emitters.put(purchaseId, emitter);

        emitter.onCompletion(() -> {
            log.info("SSE concluído para compra {}", purchaseId);
            emitters.remove(purchaseId);
        });

        emitter.onTimeout(() -> {
            log.info("SSE timeout para compra {}", purchaseId);
            emitter.complete();
            emitters.remove(purchaseId);
        });

        emitter.onError((e) -> {
            log.error("SSE erro para compra {}", purchaseId, e);
            emitter.completeWithError(e);
            emitters.remove(purchaseId);
        });

        return emitter;
    }

    public void notifyPaymentUpdate(Long purchaseId, PaymentStatusUpdateEventDTO update) {
        SseEmitter emitter = emitters.get(purchaseId);
        if (emitter != null) {
            asyncExecutor.submit(() -> {
                try {
                    emitter.send(SseEmitter.event()
                            .name("payment-update")
                            .data(update));

                    if (update.paymentStatus().isTerminal()) {
                        emitter.complete();
                        emitters.remove(purchaseId);
                    }
                } catch (IOException e) {
                    log.error("Erro ao enviar notificação SSE para compra {}", purchaseId, e);
                    emitter.completeWithError(e);
                    emitters.remove(purchaseId);
                }
            });
        }
    }
}
