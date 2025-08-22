package com.example.cinema.api.domain.pricing.strategy;

import com.example.cinema.api.domain.entities.MovieSession;

import java.math.BigDecimal;

public class RegularPricing implements PricingStrategy {
    public BigDecimal calculatePrice(MovieSession session) {
        return session.getBasePrice();
    }
}


