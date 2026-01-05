package com.example.cinema.api.domain.pricing.strategy;

import com.example.cinema.api.domain.entities.MovieSession;
import com.example.cinema.api.domain.enums.UserCategory;

import java.math.BigDecimal;
import java.time.DayOfWeek;

public interface PricingStrategy {

    BigDecimal calculateBasePrice(MovieSession session);

    UserCategory getType();
}
