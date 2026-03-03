package com.example.cinema.api.infrastructure.security.exception;

import com.auth0.jwt.exceptions.JWTVerificationException;

public class TokenValidationException extends RuntimeException {
    public TokenValidationException(String tokenInválidoOuExpirado, JWTVerificationException e) {
    }
}
