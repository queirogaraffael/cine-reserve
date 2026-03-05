package com.example.cinema.api.domain.seatreservation;

import com.example.cinema.api.domain.movie.MovieSession;
import com.example.cinema.api.domain.seatreservation.exception.ReservationCannotBeCancelledException;
import com.example.cinema.api.domain.seatreservation.exception.SeatReservationExpiredException;
import com.example.cinema.api.domain.user.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Table(name = "seat_reservations")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SeatReservation {

    private static final int EXPIRATION_MINUTES = 10;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    private MovieSession movieSession;

    @Column(nullable = false)
    private Integer seatNumber;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    private LocalDateTime expiresAt;

    public SeatReservation(MovieSession movieSession, Integer seatNumber, User user) {
        this.movieSession = movieSession;
        this.seatNumber = seatNumber;
        this.user = user;

        this.status = ReservationStatus.RESERVED;
        this.expiresAt = LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public void consume() {
        if (isExpired()) {
            throw new SeatReservationExpiredException("Reserva expirada");
        }
        this.status = ReservationStatus.CONSUMED;
    }

    public void cancel() {
        if (status != ReservationStatus.RESERVED) {
            throw new ReservationCannotBeCancelledException("Reserva não pode ser mais cancelada");
        }
        this.status = ReservationStatus.CANCELLED;
    }

}
