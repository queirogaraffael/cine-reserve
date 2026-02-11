package com.example.cinema.api.domain.ticket.context;

import com.example.cinema.api.domain.movie.MovieSession;
import com.example.cinema.api.domain.ticket.TicketCategory;
import com.example.cinema.api.domain.ticket.promotion.Promotion;
import com.example.cinema.api.domain.ticket.strategy.PricingStrategy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TicketPricingContext {

    private final Map<TicketCategory, PricingStrategy> pricingStrategies;
    private final List<Promotion> promotions;

    public TicketPricingContext(
            List<PricingStrategy> strategies,
            List<Promotion> promotions
    ) {
        this.pricingStrategies = strategies.stream()
                .collect(Collectors.toMap(PricingStrategy::getType, Function.identity()));
        this.promotions = promotions;
    }

    public BigDecimal calculate(TicketCategory ticketCategory, MovieSession session) {

        PricingStrategy strategy = pricingStrategies.get(ticketCategory);

        if (strategy == null) {
            throw new IllegalArgumentException("Categoria inválida: " + ticketCategory);
        }

        BigDecimal price = strategy.calculateBasePrice(session);

        for (Promotion promotion : promotions) {
            if (promotion.applies(session)) {
                price = promotion.apply(price);
            }
        }

        return price;
    }
}
