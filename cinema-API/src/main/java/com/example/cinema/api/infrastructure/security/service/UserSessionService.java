package com.example.cinema.api.infrastructure.security.service;

import com.example.cinema.api.application.dto.login.UserSessionDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class UserSessionService {

    @Value("${api.security.refresh.expiration}")
    private Long refreshDays;

    private final RedisTemplate<String, String> redisTemplate;
    private final HmacService hashService;
    private final ObjectMapper objectMapper;

    private static final SecureRandom secureRandom = new SecureRandom();

    public UserSessionService(RedisTemplate<String, String> redisTemplate, HmacService hashService, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.hashService = hashService;
        this.objectMapper = objectMapper;
    }

    public String createUserSession(UUID userId, String deviceId, String userAgent, String ip) {
        byte[] randomBytes = new byte[64];

        secureRandom.nextBytes(randomBytes);

        String refreshToken = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

        String hashedToken = hashService.hmacSha256(refreshToken);

        UserSessionDTO session = new UserSessionDTO(userId, deviceId, userAgent, ip, Instant.now());

        try {
            String sessionJson = objectMapper.writeValueAsString(session);

            // sessão do usuario
            redisTemplate.opsForValue().set(buildTokenKey(hashedToken), sessionJson, refreshDays, TimeUnit.DAYS);

            // para saber quantas/quais sessões atuais do usuario
            redisTemplate.opsForSet().add(buildUserSessionsKey(userId), hashedToken);

            redisTemplate.expire(buildUserSessionsKey(userId), refreshDays, TimeUnit.DAYS);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Falha ao serializar sessão do usuário: " + userId, e);
        }

        return refreshToken;
    }

    public UserSessionDTO getSession(String refreshToken) {
        String hashedToken = hashService.hmacSha256(refreshToken);

        String sessionJson = redisTemplate.opsForValue().get(buildTokenKey(hashedToken));

        if (sessionJson == null) return null;

        try {
            return objectMapper.readValue(sessionJson, UserSessionDTO.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Falha ao desserializar sessão do refresh token", e);
        }
    }

    public UUID getUserIdFromSession(String refreshToken) {
        UserSessionDTO session = getSession(refreshToken);

        if (session == null) return null;

        return session.getUserId();
    }

    public boolean validateSession(String refreshToken) {
        String hashedToken = hashService.hmacSha256(refreshToken);

        return redisTemplate.hasKey(buildTokenKey(hashedToken));
    }

    public void invalidateSession(String refreshToken) {

        String hashedToken = hashService.hmacSha256(refreshToken);

        String tokenKey = buildTokenKey(hashedToken);

        String sessionJson = redisTemplate.opsForValue().get(tokenKey);

        redisTemplate.delete(tokenKey);

        if (sessionJson != null) {
            try {
                UserSessionDTO session = objectMapper.readValue(sessionJson, UserSessionDTO.class);
                redisTemplate.opsForSet().remove(buildUserSessionsKey(session.getUserId()), hashedToken);
            } catch (JsonProcessingException e) {
                throw new IllegalStateException("Falha ao desserializar sessão de usuario", e);
            }
        }
    }

    public void invalidateAllUserSessions(UUID userId) {
        Set<String> tokens = redisTemplate.opsForSet().members(buildUserSessionsKey(userId));

        if (tokens == null) return;

        for (String hashedToken : tokens) {
            redisTemplate.delete(buildTokenKey(hashedToken));
        }

        redisTemplate.delete(buildUserSessionsKey(userId));
    }

    public Set<String> getValidSessions(UUID userId) {
        String key = buildUserSessionsKey(userId);

        Set<String> tokens = redisTemplate.opsForSet().members(key);

        if (tokens == null) return Set.of();

        Set<String> valid = new HashSet<>();

        for (String token : tokens) {
            Boolean exists = redisTemplate.hasKey(buildTokenKey(token));
            if (exists) {
                valid.add(token);
            } else {
                redisTemplate.opsForSet().remove(key, token);
            }
        }
        return valid;
    }

    private String buildTokenKey(String hashedToken) {
        return "auth:refresh_token:" + hashedToken;
    }

    private String buildUserSessionsKey(UUID userId) {
        return "auth:user_sessions:" + userId;
    }

}
