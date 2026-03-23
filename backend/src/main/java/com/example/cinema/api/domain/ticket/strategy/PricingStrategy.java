package com.example.cinema.api.domain.ticket.strategy;

import com.example.cinema.api.domain.movie.MovieSession;
import com.example.cinema.api.domain.ticket.TicketCategory;

import java.math.BigDecimal;

public interface PricingStrategy {

    BigDecimal calculateBasePrice(MovieSession session);

    TicketCategory getType();
}
