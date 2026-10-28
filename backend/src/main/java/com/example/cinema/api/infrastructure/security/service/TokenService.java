package com.example.cinema.api.infrastructure.security.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.cinema.api.domain.user.User;
import com.example.cinema.api.domain.user.UserRole;
import com.example.cinema.api.domain.user.exception.UserNotFoundException;
import com.example.cinema.api.infrastructure.persistence.UserRepositoryJpa;
import com.example.cinema.api.infrastructure.security.AuthenticatedUser;
import com.example.cinema.api.infrastructure.security.exception.TokenCreationException;
import com.example.cinema.api.infrastructure.security.exception.TokenValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
            List<String> authorities = user.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            var builder = JWT.create()
                    .withIssuer(issuer)
                    .withSubject(user.getId().toString())
                    .withClaim("roles", authorities)
                    .withIssuedAt(new Date())
                    .withExpiresAt(Date.from(Instant.now().plus(expirationHours, ChronoUnit.HOURS)));

            if (user.getRole() == UserRole.CINEMA_ADMIN && user.getCinema() != null) {
                builder.withClaim("cinemaId", user.getCinema().getId());
            }

            return builder.sign(algorithm);
        } catch (JWTCreationException e) {
            throw new TokenCreationException("Erro ao gerar o token JWT", e);
        }
    }

    public AuthenticatedUser validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            DecodedJWT decoded = JWT.require(algorithm)
                    .withIssuer(issuer)
                    .build()
                    .verify(token);

            UUID userId = UUID.fromString(decoded.getSubject());
            List<String> roles = decoded.getClaim("roles").asList(String.class);
            List<SimpleGrantedAuthority> authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .toList();

            Long cinemaId = null;
            if (!decoded.getClaim("cinemaId").isNull()) {
                cinemaId = decoded.getClaim("cinemaId").asLong();
            }

            return new AuthenticatedUser(userId, authorities, cinemaId);
        } catch (JWTVerificationException e) {
            throw new TokenValidationException("Token inválido ou expirado", e);
        }
    }

    public String generateJwt(UUID userId) {
        User user = userRepositoryJpa.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));
        return generateToken(user);
    }

}
