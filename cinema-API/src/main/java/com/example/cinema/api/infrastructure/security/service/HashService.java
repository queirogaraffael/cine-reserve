package com.example.cinema.api.infrastructure.security.service;

import org.springframework.stereotype.Service;

import java.util.HexFormat;

import org.springframework.beans.factory.annotation.Value;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Service
public class HashService {

    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private final SecretKeySpec keySpec;

    public HashService(@Value("${security.hash.secret}") String secret) {
        this.keySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM);
    }

    public String hmacSha256(String value) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);

            mac.init(keySpec);

            byte[] hash = mac.doFinal(value.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new IllegalStateException("Erro ao gerar HMAC SHA256 hash", e);
        }
    }
}
