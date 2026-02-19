package com.example.cinema.api.infrastructure.security.service;

import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Service
public class HashService {

    private static final String HASH_ALGORITHM = "SHA-256";

    public String sha256(String value) {

        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);

            byte[] hash = digest.digest(value.getBytes());

            return HexFormat.of().formatHex(hash);

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Hash algorithm " + HASH_ALGORITHM + " não está disponivel.", e);
        }
    }
}

