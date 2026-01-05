package com.example.cinema.api.domain.pricing.strategy;

import com.example.cinema.api.domain.entities.MovieSession;
import com.example.cinema.api.domain.enums.UserCategory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class RegularPricing implements PricingStrategy {

    public BigDecimal calculateBasePrice(MovieSession session) {
        return session.getBasePrice();
    }

    public UserCategory getType() {
        return UserCategory.REGULAR;
    }
}
