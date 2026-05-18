package com.example.cinema.api.infrastructure.security.service;

import com.example.cinema.api.shared.util.HmacUtils;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Value;


@Service
public class HmacService {

    private final String secret;

    public HmacService(@Value("${security.hash.secret}") String secret) {
        this.secret = secret;
    }

    public String hmacSha256(String value) {
        return HmacUtils.compute(value, secret);
    }
}