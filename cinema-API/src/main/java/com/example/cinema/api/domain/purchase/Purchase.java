package com.example.cinema.api.domain.purchase;

import com.example.cinema.api.domain.purchase.exception.PurchaseModificationNotAllowedException;
import com.example.cinema.api.domain.ticket.exception.InvalidTicketPriceException;
import com.example.cinema.api.domain.exception.SeatReservationRequiredException;
import com.example.cinema.api.domain.seatreservation.SeatReservation;
import com.example.cinema.api.domain.ticket.Ticket;
import com.example.cinema.api.domain.ticket.TicketCategory;
import com.example.cinema.api.domain.payment.Payment;
import com.example.cinema.api.domain.user.User;
import com.example.cinema.api.domain.seatreservation.exception.SeatReservationExpiredException;
import com.example.cinema.api.domain.exception.IdempotencyKeyRequiredException;
import com.example.cinema.api.domain.exception.UserRequiredException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
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

    @Enumerated
    @NotNull
    private PurchaseStatus purchaseStatus;

    public Purchase(User user, String idempotencyKey) {

        if (user == null) {
            throw new UserRequiredException("Usuário é obrigatório para criar uma compra.");
        }

        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new IdempotencyKeyRequiredException("A chave de idempotência é obrigatória.");
        }

        this.user = user;
        this.idempotencyKey = idempotencyKey;
        this.purchaseDate = LocalDateTime.now();
        this.purchaseStatus = PurchaseStatus.CREATED;
        this.totalPrice = BigDecimal.ZERO;
    }

    public void addTicket(SeatReservation reservation, TicketCategory category, BigDecimal price) {

        if (this.purchaseStatus != PurchaseStatus.CREATED) {
            throw new PurchaseModificationNotAllowedException("Não é possível adicionar ingressos a uma compra que já não tenha Status CREATED.");
        }

        if (reservation == null) {
            throw new SeatReservationRequiredException("Reserva do assento é obrigatória.");
        }

        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTicketPriceException("O preço do ingresso deve ser maior que zero.");
        }

        if (reservation.isExpired()) {
            throw new SeatReservationExpiredException("Reserva expirada.");
        }

        Ticket ticket = new Ticket(
                reservation.getSeatNumber(),
                reservation.getMovieSession(),
                category,
                price,
                this);

        this.tickets.add(ticket);
        this.totalPrice = this.totalPrice.add(price);
    }
}
