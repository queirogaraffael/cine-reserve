package com.example.cinema.api.domain.purchase;

import com.example.cinema.api.domain.seatreservation.SeatReservation;
import com.example.cinema.api.domain.ticket.Ticket;
import com.example.cinema.api.domain.ticket.TicketCategory;
import com.example.cinema.api.domain.payment.Payment;
import com.example.cinema.api.domain.user.User;
import com.example.cinema.api.shared.exceptions.SeatReservationExpiredException;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private LocalDateTime purchaseDate;
    private BigDecimal totalPrice;

    @Column(name = "idempotency_key", nullable = false, unique = true, length = 36)
    private String idempotencyKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "purchase", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Size(min = 1)
    private Set<Ticket> tickets = new HashSet<>();

    @OneToOne(mappedBy = "purchase", cascade = CascadeType.ALL)
    private Payment payment;

    public Purchase(User user, String idempotencyKey) {
        this.user = user;
        this.idempotencyKey = idempotencyKey;
        this.purchaseDate = LocalDateTime.now();
    }

    public void addTicket(SeatReservation reservation, TicketCategory category, BigDecimal price) {

        if (reservation.isExpired()) {
            throw new SeatReservationExpiredException("Reserva expirada");
        }

        Ticket ticket = new Ticket(reservation.getSeatNumber(), reservation.getMovieSession(), category, price, this);

        this.tickets.add(ticket);
        this.totalPrice = this.totalPrice.add(price);
    }
}
