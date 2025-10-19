package com.example.cinema.api.domain.pricing.strategy;

import com.example.cinema.api.domain.entities.MovieSession;
import com.example.cinema.api.domain.enums.UserCategory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class SeniorPricing implements PricingStrategy {
    public BigDecimal calculatePrice(MovieSession session) {
        return session.getBasePrice().multiply(new BigDecimal("0.7"));
    }

    public UserCategory getType() {
        return UserCategory.SENIOR;
    }
}
