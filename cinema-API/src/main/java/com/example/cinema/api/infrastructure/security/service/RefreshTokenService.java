package com.example.cinema.api.infrastructure.security.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class RefreshTokenService {

    @Value("${api.security.refresh.expiration}")
    private Long refreshDays;

    private final RedisTemplate<String, String> refreshTokenRedisTemplate;

    private final HashService hashService;

    public RefreshTokenService(RedisTemplate<String, String> refreshTokenRedisTemplate, HashService hashService) {
        this.refreshTokenRedisTemplate = refreshTokenRedisTemplate;
        this.hashService = hashService;
    }

    public String generateRefreshToken(String username) {

        String refreshToken = UUID.randomUUID().toString();

        String hashedToken = hashService.sha256(refreshToken);

        refreshTokenRedisTemplate.opsForValue().set(buildKey(hashedToken), username, refreshDays, TimeUnit.DAYS);

        return refreshToken;
    }

    public boolean validateRefreshToken(String refreshToken) {
        String hashedToken = hashService.sha256(refreshToken);

        return refreshTokenRedisTemplate.opsForValue()
                .get(buildKey(hashedToken)) != null;

    }

    public String getUsernameFromRefreshToken(String refreshToken) {
        String hashedToken = hashService.sha256(refreshToken);

        return refreshTokenRedisTemplate.opsForValue().get(buildKey(hashedToken));
    }

    public void invalidateRefreshToken(String refreshToken) {
        String hashedToken = hashService.sha256(refreshToken);

        refreshTokenRedisTemplate.delete(buildKey(hashedToken));
    }

    private String buildKey(String hashedToken) {
        return "auth:refresh_token:" + hashedToken;
    }

}
