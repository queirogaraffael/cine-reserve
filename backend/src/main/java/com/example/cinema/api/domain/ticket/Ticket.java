package com.example.cinema.api.domain.ticket;

import com.example.cinema.api.domain.movie.MovieSession;
import com.example.cinema.api.domain.purchase.Purchase;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(
        name = "tickets",
        uniqueConstraints = @UniqueConstraint(columnNames = {"session_id", "seat_number"}))
@Entity
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private Long id;

    private int seatNumber;

    @Enumerated(EnumType.STRING)
    private TicketCategory category;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    @ToString.Exclude
    private MovieSession movieSession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_id", nullable = false)
    @ToString.Exclude
    private Purchase purchase;

    private BigDecimal price;

    public Ticket(Integer seatNumber, MovieSession movieSession, TicketCategory category, BigDecimal price, Purchase purchase) {

        this.seatNumber = seatNumber;
        this.category = category;
        this.price = price;
        this.purchase = purchase;

        movieSession.addTicket(this);
    }

}