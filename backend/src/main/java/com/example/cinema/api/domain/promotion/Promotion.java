package com.example.cinema.api.domain.promotion;

import com.example.cinema.api.domain.cinema.Cinema;
import com.example.cinema.api.domain.movie.MovieSession;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.DayOfWeek;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "promotions")
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cinema_id", nullable = false)
    private Cinema cinema;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week")
    private DayOfWeek dayOfWeek;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_session_id")
    private MovieSession targetSession;

    @Column(precision = 5, scale = 2)
    private BigDecimal discountPercentage;

    @Column(precision = 10, scale = 2)
    private BigDecimal fixedPrice;

    @Column(nullable = false)
    private boolean active = true;

    public Promotion(String name, Cinema cinema, DayOfWeek dayOfWeek, BigDecimal discountPercentage) {
        this.name = name;
        this.cinema = cinema;
        this.dayOfWeek = dayOfWeek;
        this.discountPercentage = discountPercentage;
        this.active = true;
    }

    public boolean appliesTo(MovieSession session) {
        if (!this.active || !this.cinema.getId().equals(session.getCinemaRoom().getCinema().getId())) {
            return false;
        }
        if (this.targetSession != null) {
            return this.targetSession.getId().equals(session.getId());
        }
        if (this.dayOfWeek != null) {
            return session.getShowDate().getDayOfWeek() == this.dayOfWeek;
        }
        return true;
    }
}
