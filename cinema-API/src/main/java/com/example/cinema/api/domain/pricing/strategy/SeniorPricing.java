package com.example.cinema.api.domain.pricing.strategy;

import com.example.cinema.api.domain.entities.MovieSession;
import com.example.cinema.api.domain.enums.TicketCategory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class SeniorPricing implements PricingStrategy {

    public BigDecimal calculateBasePrice(MovieSession session) {
        return session.getBasePrice().multiply(new BigDecimal("0.7"));
    }

    public TicketCategory getType() {
        return TicketCategory.SENIOR;
    }
}

