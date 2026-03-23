package com.example.cinema.api.domain.ticket.strategy;

import com.example.cinema.api.domain.movie.MovieSession;
import com.example.cinema.api.domain.ticket.TicketCategory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class RegularPricing implements PricingStrategy {

    public BigDecimal calculateBasePrice(MovieSession session) {
        return session.getBasePrice();
    }

    public TicketCategory getType() {
        return TicketCategory.REGULAR;
    }
}
