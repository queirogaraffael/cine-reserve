package com.example.cinema.api.infrastructure.security.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class RefreshTokenService {

    @Value("${api.security.refresh.expiration}")
    private Long refreshDays;

    private final RedisTemplate<String, String> refreshTokenRedisTemplate;

    private final HashService hashService;

    private static final SecureRandom secureRandom = new SecureRandom();

    public RefreshTokenService(RedisTemplate<String, String> refreshTokenRedisTemplate, HashService hashService) {
        this.refreshTokenRedisTemplate = refreshTokenRedisTemplate;
        this.hashService = hashService;
    }

    public String generateRefreshToken(UUID userID) {

        byte[] randomBytes = new byte[64];

        secureRandom.nextBytes(randomBytes);

        String refreshToken = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

        String hashedToken = hashService.sha256(refreshToken);

        refreshTokenRedisTemplate.opsForValue().set(buildKey(hashedToken), userID.toString(), refreshDays, TimeUnit.DAYS);

        return refreshToken;
    }

    public boolean validateRefreshToken(String refreshToken) {
        String hashedToken = hashService.sha256(refreshToken);

        return refreshTokenRedisTemplate.opsForValue()
                .get(buildKey(hashedToken)) != null;

    }

    public UUID getUserIdFromRefreshToken(String refreshToken) {

        String hashedToken = hashService.sha256(refreshToken);

        String userIdString = refreshTokenRedisTemplate.opsForValue().get(buildKey(hashedToken));

        if (userIdString == null) {
            return null;
        }
        return UUID.fromString(userIdString);
    }


    public void invalidateRefreshToken(String refreshToken) {
        String hashedToken = hashService.sha256(refreshToken);

        refreshTokenRedisTemplate.delete(buildKey(hashedToken));
    }

    private String buildKey(String hashedToken) {
        return "auth:refresh_token:" + hashedToken;
    }

}
