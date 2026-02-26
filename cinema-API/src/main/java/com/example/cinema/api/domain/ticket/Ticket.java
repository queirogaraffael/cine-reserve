package com.example.cinema.api.domain.ticket;

import com.example.cinema.api.domain.movie.MovieSession;
import com.example.cinema.api.domain.purchase.Purchase;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "tickets", uniqueConstraints = @UniqueConstraint(columnNames = {"session_id", "seat_number"}))
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private int seatNumber;

    @Enumerated(EnumType.STRING)
    private TicketCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    private MovieSession movieSession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_id")
    private Purchase purchase;

    private BigDecimal price;

    public Ticket(Integer seatNumber, MovieSession movieSession, TicketCategory category, BigDecimal price, Purchase purchase) {
        this.seatNumber = seatNumber;
        this.movieSession = movieSession;
        this.category = category;
        this.price = price;
        this.purchase = purchase;
    }
}
