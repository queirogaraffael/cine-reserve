package com.example.cinema.api.domain.pricing.strategy;

import com.example.cinema.api.domain.entities.MovieSession;
import com.example.cinema.api.domain.enums.TicketCategory;

import java.math.BigDecimal;

public interface PricingStrategy {

    BigDecimal calculateBasePrice(MovieSession session);

    TicketCategory getType();
}
