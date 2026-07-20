package com.example.cinema.api.infrastructure.mercadopago;

import com.example.cinema.api.shared.util.HmacUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import java.time.Instant;
import java.util.Locale;

@Component
public class MercadoPagoWebhookValidator {

    private static final long TOLERANCE_SECONDS = 300L;

    @Value("${mercadopago.webhook.secret}")
    private String secret;

    private final ObjectMapper objectMapper;

    public MercadoPagoWebhookValidator(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public boolean isValid(String signatureHeader, String requestId, String payload) {
        if (signatureHeader == null || requestId == null || payload == null) {
            return false;
        }

        String ts = extractField(signatureHeader, "ts");
        String v1 = extractField(signatureHeader, "v1");

        if (ts == null || v1 == null) {
            return false;
        }

        if (isTimestampOutOfTolerance(ts)) {
            return false;
        }

        String manifest = "id:" + extractDataId(payload) + ";request-id:" + requestId + ";ts:" + ts + ";";

        String computed = HmacUtils.compute(manifest, secret);

        return MessageDigest.isEqual(
                computed.toLowerCase(Locale.ROOT).getBytes(StandardCharsets.UTF_8),
                v1.toLowerCase(Locale.ROOT).getBytes(StandardCharsets.UTF_8)
        );
    }

    private boolean isTimestampOutOfTolerance(String ts) {
        try {
            long webhookEpochSeconds = Long.parseLong(ts);
            long currentEpochSeconds = Instant.now().getEpochSecond();
            return Math.abs(currentEpochSeconds - webhookEpochSeconds) > TOLERANCE_SECONDS;
        } catch (NumberFormatException e) {
            return true;
        }
    }

    private String extractField(String header, String key) {
        for (String part : header.split(",")) {
            String trimmed = part.trim();
            if (trimmed.startsWith(key + "=")) {
                return trimmed.substring(key.length() + 1);
            }
        }
        return null;
    }

    private String extractDataId(String payload) {
        try {
            JsonNode node = objectMapper.readTree(payload);
            JsonNode dataId = node.path("data").path("id");
            return dataId.isMissingNode() ? "" : dataId.asText();
        } catch (Exception e) {
            return "";
        }
    }
}
