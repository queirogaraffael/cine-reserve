package com.example.cinema.api.domain.pricing.context;

import com.example.cinema.api.domain.entities.MovieSession;
import com.example.cinema.api.domain.entities.User;
import com.example.cinema.api.domain.enums.UserCategory;
import com.example.cinema.api.domain.pricing.strategy.PricingStrategy;
import com.example.cinema.api.domain.pricing.strategy.WednesdayPromoPricing;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TicketPricingContext {

    private final Map<UserCategory, PricingStrategy> pricingStrategies;
    private WednesdayPromoPricing wednesdayPromoPricing = new WednesdayPromoPricing();

    public TicketPricingContext(List<PricingStrategy> pricingStrategies, WednesdayPromoPricing wednesdayPromoPricing) {
        this.pricingStrategies = pricingStrategies.stream()
                .collect(Collectors.toMap(PricingStrategy::getType, Function.identity()));
        this.wednesdayPromoPricing = wednesdayPromoPricing;
    }

    public BigDecimal executeStrategy(User user, MovieSession session) {

        PricingStrategy strategy = pricingStrategies.get(user.getCategory());

        BigDecimal finalPrice = strategy.calculatePrice(session);

        if (strategy.isWednesdayPromo(session)) {
            finalPrice = wednesdayPromoPricing.calculatePrice(finalPrice);
        }

        return finalPrice;
    }
}
