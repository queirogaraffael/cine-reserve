package com.example.cinema.api.domain.ticket.promotion;

import com.example.cinema.api.domain.movie.MovieSession;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.DayOfWeek;

@Component
public class WednesdayPromotion implements Promotion {

    public boolean applies(MovieSession session) {
        return session.getShowDate().getDayOfWeek() == DayOfWeek.WEDNESDAY;
    }

    public BigDecimal apply(BigDecimal currentPrice) {
        return currentPrice.multiply(new BigDecimal("0.5"));
    }
}
