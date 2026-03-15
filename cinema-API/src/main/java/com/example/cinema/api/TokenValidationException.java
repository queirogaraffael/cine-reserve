package com.example.cinema.api;

import com.auth0.jwt.exceptions.JWTVerificationException;

public class TokenValidationException extends RuntimeException {
    public TokenValidationException(String message, JWTVerificationException cause) {
        super(message, cause);
    }
}
