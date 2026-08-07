package com.example.cinema.api.domain.order;

import com.example.cinema.api.domain.movie.MovieSession;
import com.example.cinema.api.domain.movie.exception.MovieSessionRequiredException;
import com.example.cinema.api.domain.order.exception.OrderModificationNotAllowedException;
import com.example.cinema.api.domain.user.User;
import com.example.cinema.api.domain.user.exception.UserRequiredException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import com.example.cinema.api.domain.common.Money;
import com.example.cinema.api.domain.payment.OrderPayment;
import com.example.cinema.api.domain.payment.PaymentStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Version
    private Long version;

    private LocalDateTime createdAt;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "total_price", nullable = false, precision = 10, scale = 2))
    })
    private Money totalPrice = Money.zero();

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "service_fee", nullable = false, precision = 10, scale = 2))
    })
    private Money serviceFee = Money.zero();

    @Column(nullable = false)
    private Integer totalTicketsCount = 0;

    private LocalDateTime reservationExpiresAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    @ToString.Exclude
    private MovieSession movieSession;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<OrderPayment> payments = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @NotNull
    private OrderStatus status;

    public Order(User user, MovieSession movieSession) {
        if (user == null) {
            throw new UserRequiredException("Usuário é obrigatório.");
        }
        if (movieSession == null) {
            throw new MovieSessionRequiredException("Sessão é obrigatória.");
        }
        this.user = user;
        this.movieSession = movieSession;
        this.createdAt = LocalDateTime.now();
        this.status = OrderStatus.CREATED;
    }

    public void addItem(OrderItem item) {
        if (status != OrderStatus.CREATED) {
            throw new OrderModificationNotAllowedException("Pedido não pode ser modificado.");
        }
        orderItems.add(item);
        this.totalPrice = this.totalPrice.add(Money.of(item.getSubtotal()));
        this.totalTicketsCount += item.getQuantity();
    }

    public void applyServiceFee(Money fee) {
        this.serviceFee = fee;
        this.totalPrice = this.totalPrice.add(fee);
    }

    public void moveToStatus(OrderStatus newStatus) {
        this.status = this.status.transitionTo(newStatus);
    }

    public void markReservationExpiry(LocalDateTime expiresAt) {
        this.reservationExpiresAt = expiresAt;
    }

    public boolean isPaid() {
        return payments.stream()
                .anyMatch(p -> p.getPaymentStatus() == PaymentStatus.APPROVED);
    }
}
