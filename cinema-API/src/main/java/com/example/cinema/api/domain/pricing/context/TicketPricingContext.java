package com.example.cinema.api.domain.pricing.context;

import com.example.cinema.api.domain.entities.MovieSession;
import com.example.cinema.api.domain.entities.User;
import com.example.cinema.api.domain.enums.UserCategory;
import com.example.cinema.api.domain.pricing.promotion.Promotion;
import com.example.cinema.api.domain.pricing.strategy.PricingStrategy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TicketPricingContext {

    private final Map<UserCategory, PricingStrategy> pricingStrategies;
    private final List<Promotion> promotions;

    public TicketPricingContext(
            List<PricingStrategy> strategies,
            List<Promotion> promotions
    ) {
        this.pricingStrategies = strategies.stream()
                .collect(Collectors.toMap(PricingStrategy::getType, Function.identity()));
        this.promotions = promotions;
    }

    public BigDecimal calculate(User user, MovieSession session) {

        PricingStrategy strategy = pricingStrategies.get(user.getCategory());

        if (strategy == null) {
            throw new IllegalArgumentException("Categoria inválida: " + user.getCategory());
        }

        BigDecimal price = strategy.calculateBasePrice(session);

        for (Promotion promotion : promotions) {
            if (promotion.applies(user, session)) {
                price = promotion.apply(price);
            }
        }

        return price;
    }
}
