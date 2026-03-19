package com.example.cinema.api.domain.seatreservation;

import com.example.cinema.api.domain.movie.exception.MovieSessionRequiredException;
import com.example.cinema.api.domain.seatreservation.exception.ReservationCannotBeConsumedException;
import com.example.cinema.api.domain.seatreservation.exception.SeatNumberRequiredException;
import com.example.cinema.api.domain.movie.MovieSession;
import com.example.cinema.api.domain.seatreservation.exception.ReservationCannotBeCancelledException;
import com.example.cinema.api.domain.seatreservation.exception.ReservationExpiredException;
import com.example.cinema.api.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "seat_reservations")
@Entity
public class SeatReservation {

    private static final int EXPIRATION_MINUTES = 10;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    @ToString.Exclude
    @Setter
    private MovieSession movieSession;

    @Column(nullable = false)
    private Integer seatNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    private LocalDateTime expiresAt;

    public SeatReservation(MovieSession movieSession, Integer seatNumber, User user) {

        if (movieSession == null)
            throw new MovieSessionRequiredException("Sessão é obrigatória.");

        if (seatNumber == null)
            throw new SeatNumberRequiredException("Número do assento é obrigatório.");

        this.seatNumber = seatNumber;
        this.user = user;

        this.status = ReservationStatus.RESERVED;
        this.expiresAt = LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES);

        movieSession.addSeatReservation(this);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public void consume() {
        if (status != ReservationStatus.RESERVED)
            throw new ReservationCannotBeConsumedException("Reserva inválida para consumo");

        if (isExpired())
            throw new ReservationExpiredException("Reserva expirada");

        this.status = ReservationStatus.CONSUMED;
    }

    public void cancel() {

        if (status != ReservationStatus.RESERVED)
            throw new ReservationCannotBeCancelledException("Reserva não pode ser cancelada");

        this.status = ReservationStatus.CANCELLED;
    }

}