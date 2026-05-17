package com.example.cinema.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Component
public class HmacValidator {

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    @Value("${mercadopago.webhook.secret}")
    private String secret;

    private final ObjectMapper objectMapper;

    public HmacValidator(ObjectMapper objectMapper) {
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

        String manifest = "id:" + extractDataId(payload) + ";request-id:" + requestId + ";ts:" + ts + ";";

        String computed = computeHmac(manifest);

        if (computed == null) {
            return false;
        }

        return MessageDigest.isEqual(
                computed.getBytes(StandardCharsets.UTF_8),
                v1.getBytes(StandardCharsets.UTF_8)
        );
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

    private String computeHmac(String data) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM));
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            return null;
        }
    }
}