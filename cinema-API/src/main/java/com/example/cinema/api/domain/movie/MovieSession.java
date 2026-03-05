package com.example.cinema.api.domain.movie;


import com.example.cinema.api.domain.movie.exception.InvalidSessionTimeRangeException;
import com.example.cinema.api.domain.seatreservation.SeatReservation;
import com.example.cinema.api.domain.room.Room;
import com.example.cinema.api.domain.ticket.Ticket;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MovieSession {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private Long id;

    private LocalDate showDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private BigDecimal basePrice;

    private boolean canceled;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room cinemaRoom;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @OneToMany(mappedBy = "movieSession", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Ticket> tickets = new ArrayList<>();

    @OneToMany(mappedBy = "movieSession", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SeatReservation> seatReservations = new ArrayList<>();

    public MovieSession(LocalDate showDate, LocalTime startTime, LocalTime endTime, BigDecimal basePrice, Room cinemaRoom, Movie movie) {

        if (!startTime.isBefore(endTime)) {
            throw new InvalidSessionTimeRangeException("A hora de início deve ser antes da hora de término.");
        }

        this.showDate = showDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.basePrice = basePrice;
        this.cinemaRoom = cinemaRoom;
        this.movie = movie;
        this.canceled = false;
    }

    @Transient
    public MovieSessionStatus getStatus() {
        if (canceled) {
            return MovieSessionStatus.CANCELED;
        }

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        if (today.isBefore(showDate) || (today.isEqual(showDate) && now.isBefore(startTime))) {
            return MovieSessionStatus.SCHEDULED;
        }
        if (today.isEqual(showDate) && (now.isAfter(startTime) || now.equals(startTime)) && now.isBefore(endTime)) {
            return MovieSessionStatus.ACTIVE;
        }
        return MovieSessionStatus.FINISHED;
    }

    @Transient
    public boolean isAvailableForPurchase() {
        return !canceled && getStatus() == MovieSessionStatus.SCHEDULED;
    }
}
