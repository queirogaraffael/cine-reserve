package com.example.cinema.api.domain.pricing.strategy;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class WednesdayPromoPricing {
    public BigDecimal calculatePrice(BigDecimal price) {
        return price.multiply(new BigDecimal("0.5"));
    }
}