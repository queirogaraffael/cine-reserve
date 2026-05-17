package com.example.cinema.api.domain.purchase;

import com.example.cinema.api.domain.purchase.exception.PurchaseAlreadyHasPaymentException;
import com.example.cinema.api.domain.purchase.exception.PurchaseModificationNotAllowedException;
import com.example.cinema.api.domain.ticket.exception.InvalidTicketPriceException;
import com.example.cinema.api.domain.seatreservation.exception.ReservationRequiredException;
import com.example.cinema.api.domain.seatreservation.SeatReservation;
import com.example.cinema.api.domain.ticket.Ticket;
import com.example.cinema.api.domain.ticket.TicketCategory;
import com.example.cinema.api.domain.payment.Payment;
import com.example.cinema.api.domain.user.User;
import com.example.cinema.api.domain.seatreservation.exception.ReservationExpiredException;
import com.example.cinema.api.domain.purchase.exception.IdempotencyKeyRequiredException;
import com.example.cinema.api.domain.user.exception.UserRequiredException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private Long id;

    @Version
    private Long version;

    private LocalDateTime purchaseDate;

    private BigDecimal totalPrice = BigDecimal.ZERO;

    @Column(name = "idempotency_key", nullable = false, unique = true, length = 36)
    private String idempotencyKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;

    @OneToMany(mappedBy = "purchase", cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter(AccessLevel.NONE)
    @ToString.Exclude
    private Set<Ticket> tickets = new HashSet<>();

    @OneToOne(mappedBy = "purchase", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Payment payment;

    @Enumerated(EnumType.STRING)
    @NotNull
    private PurchaseStatus purchaseStatus;

    public Purchase(User user, String idempotencyKey) {

        if (user == null)
            throw new UserRequiredException("Usuário é obrigatório.");

        if (idempotencyKey == null || idempotencyKey.isBlank())
            throw new IdempotencyKeyRequiredException("Chave de idempotência obrigatória.");

        this.user = user;
        this.idempotencyKey = idempotencyKey;
        this.purchaseDate = LocalDateTime.now();
        this.purchaseStatus = PurchaseStatus.CREATED;
    }

    public void addTicket(SeatReservation reservation, TicketCategory category, BigDecimal price) {

        if (purchaseStatus != PurchaseStatus.CREATED)
            throw new PurchaseModificationNotAllowedException("Compra não pode ser modificada.");

        if (reservation == null)
            throw new ReservationRequiredException("Reserva obrigatória.");

        if (reservation.isExpired())
            throw new ReservationExpiredException("Reserva expirada.");

        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0)
            throw new InvalidTicketPriceException("Preço inválido.");

        Ticket ticket = new Ticket(reservation.getSeatNumber(), reservation.getMovieSession(), category, price, this);

        tickets.add(ticket);
        totalPrice = totalPrice.add(price);

        reservation.consume();
    }

    public void moveToStatus(PurchaseStatus newStatus) {
        this.purchaseStatus = this.purchaseStatus.transitionTo(newStatus);
    }

    public void changeIdempotencyKey(String newKey) {

        if (newKey == null || newKey.isBlank()) {
            throw new IdempotencyKeyRequiredException("A chave de idempotência é obrigatória.");
        }

        if (this.payment != null) {
            throw new PurchaseAlreadyHasPaymentException("IdempotencyKey não pode ser modificada porque um pagamento já está associado.");
        }

        this.idempotencyKey = newKey;
    }

    public void attachPayment(Payment payment) {

        if (payment == null)
            throw new IllegalArgumentException("Pagamento não pode ser nulo.");

        if (this.payment != null)
            throw new PurchaseAlreadyHasPaymentException("Compra já possui pagamento.");

        this.payment = payment;
    }

}
