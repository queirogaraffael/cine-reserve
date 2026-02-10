package com.example.cinema.api.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.cinema.api.domain.entities.User;
import com.example.cinema.api.infrastructure.persistence.UserRepositoryJpa;
import com.example.cinema.api.shared.exceptions.TokenCreationException;
import com.example.cinema.api.shared.exceptions.TokenValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    @Value("${api.security.token.expiration}")
    private Long expirationHours;

    @Value("${api.security.token.issuer}")
    private String issuer;

    @Value("${api.security.refresh.expiration}")
    private Long refreshDays;

    private final RedisTemplate<String, Object> redisTemplate;

    private final UserRepositoryJpa userRepositoryJpa;

    public TokenService(RedisTemplate<String, Object> redisTemplate, UserRepositoryJpa userRepositoryJpa) {
        this.redisTemplate = redisTemplate;
        this.userRepositoryJpa = userRepositoryJpa;
    }

    public String generateToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer(issuer)
                    .withSubject(user.getUsername())
                    .withIssuedAt(new Date())
                    .withExpiresAt(Date.from(Instant.now().plus(expirationHours, ChronoUnit.HOURS)))
                    .sign(algorithm);
        } catch (JWTCreationException e) {
            throw new TokenCreationException("Erro ao gerar o token JWT", e);
        }
    }

    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            DecodedJWT decoded = JWT.require(algorithm)
                    .withIssuer(issuer)
                    .build()
                    .verify(token);
            return decoded.getSubject();
        } catch (JWTVerificationException e) {
            throw new TokenValidationException("Token inválido ou expirado", e);
        }
    }

    public String generateRefreshToken(String username) {
        String refreshToken = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set("refresh:" + refreshToken, username, refreshDays, TimeUnit.DAYS);
        return refreshToken;
    }

    public boolean validateRefreshToken(String refreshToken) {
        return redisTemplate.opsForValue().get("refresh:" + refreshToken) != null;
    }

    public String getUsernameFromRefreshToken(String refreshToken) {
        return (String) redisTemplate.opsForValue().get("refresh:" + refreshToken);
    }

    public void invalidateRefreshToken(String refreshToken) {
        redisTemplate.delete("refresh:" + refreshToken);
    }

    public String generateJwt(String username) {
        User user = userRepositoryJpa.findByUsername(username)
                .orElseThrow(() -> new TokenValidationException("Usuário não encontrado", null));
        return generateToken(user);
    }

}
