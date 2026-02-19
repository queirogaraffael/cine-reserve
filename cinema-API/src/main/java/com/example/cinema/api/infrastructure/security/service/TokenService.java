package com.example.cinema.api.infrastructure.security.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.cinema.api.domain.user.User;
import com.example.cinema.api.infrastructure.persistence.UserRepositoryJpa;
import com.example.cinema.api.shared.exceptions.TokenCreationException;
import com.example.cinema.api.shared.exceptions.TokenValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    @Value("${api.security.token.expiration}")
    private Long expirationHours;

    @Value("${api.security.token.issuer}")
    private String issuer;

    private final UserRepositoryJpa userRepositoryJpa;

    public TokenService(UserRepositoryJpa userRepositoryJpa) {
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

    public String generateJwt(String username) {
        User user = userRepositoryJpa.findByUsername(username)
                .orElseThrow(() -> new TokenValidationException("Usuário não encontrado", null));
        return generateToken(user);
    }

}
