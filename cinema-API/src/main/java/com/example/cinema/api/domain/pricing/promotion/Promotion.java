package com.example.cinema.api.domain.pricing.promotion;

import com.example.cinema.api.domain.entities.MovieSession;
import com.example.cinema.api.domain.entities.User;

import java.math.BigDecimal;

public interface Promotion {

    boolean applies(User user, MovieSession session);

    BigDecimal apply(BigDecimal currentPrice);
}
