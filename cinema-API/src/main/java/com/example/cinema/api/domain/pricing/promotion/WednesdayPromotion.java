package com.example.cinema.api.domain.pricing.promotion;

import com.example.cinema.api.domain.entities.MovieSession;
import com.example.cinema.api.domain.entities.User;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.DayOfWeek;

@Component
public class WednesdayPromotion implements Promotion {

    public boolean applies(User user, MovieSession session) {
        return session.getShowDate().getDayOfWeek() == DayOfWeek.WEDNESDAY;
    }

    public BigDecimal apply(BigDecimal currentPrice) {
        return currentPrice.multiply(new BigDecimal("0.5"));
    }
}
