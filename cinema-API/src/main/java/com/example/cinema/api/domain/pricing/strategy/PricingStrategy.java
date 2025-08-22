package com.example.cinema.api.domain.pricing.strategy;

import com.example.cinema.api.domain.entities.MovieSession;

import java.math.BigDecimal;

public interface PricingStrategy {
    BigDecimal calculatePrice(MovieSession session);
}
