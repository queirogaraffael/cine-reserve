package com.example.cinema.api.domain.pricing.context;

import com.example.cinema.api.domain.entities.MovieSession;
import com.example.cinema.api.domain.pricing.strategy.PricingStrategy;

import java.math.BigDecimal;

public class TicketPricingContext {
    private PricingStrategy strategy;

    public TicketPricingContext(PricingStrategy strategy) {
        this.strategy = strategy;
    }

    public BigDecimal executeStrategy(MovieSession session) {
        return strategy.calculatePrice(session);
    }
}
