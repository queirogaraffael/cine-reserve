package com.example.cinema.api.domain.ticket.promotion;

import com.example.cinema.api.domain.movie.MovieSession;

import java.math.BigDecimal;

public interface Promotion {

    boolean applies(MovieSession session);

    BigDecimal apply(BigDecimal currentPrice);
}
