package com.example.cinema.api.domain.seatreservation;

import com.example.cinema.api.domain.movie.MovieSession;
import com.example.cinema.api.domain.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "seat_reservations")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeatReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public void consume() {
        if (isExpired()) {
            throw new IllegalStateException("Reserva expirada");
        }
        this.status = ReservationStatus.CONSUMED;
    }
}
