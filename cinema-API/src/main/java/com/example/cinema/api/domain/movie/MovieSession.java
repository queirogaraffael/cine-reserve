package com.example.cinema.api.domain.movie;


import com.example.cinema.api.domain.movie.exception.MovieRequiredException;
import com.example.cinema.api.domain.room.exception.RoomRequiredException;
import com.example.cinema.api.InvalidSessionTimeRangeException;
import com.example.cinema.api.domain.seatreservation.SeatReservation;
import com.example.cinema.api.domain.room.Room;
import com.example.cinema.api.domain.ticket.Ticket;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
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
    @ToString.Exclude
    private Room cinemaRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    @ToString.Exclude
    private Movie movie;

    @OneToMany(mappedBy = "movieSession", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Setter(AccessLevel.NONE)
    @ToString.Exclude
    private List<Ticket> tickets = new ArrayList<>();

    @OneToMany(mappedBy = "movieSession", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Setter(AccessLevel.NONE)
    @ToString.Exclude
    private List<SeatReservation> seatReservations = new ArrayList<>();

    public MovieSession(LocalDate showDate, LocalTime startTime, LocalTime endTime, BigDecimal basePrice, Room cinemaRoom, Movie movie) {

        if (startTime == null || endTime == null || !startTime.isBefore(endTime)) {
            throw new InvalidSessionTimeRangeException("A hora de início deve ser antes da hora de término.");
        }

        if (cinemaRoom == null) {
            throw new RoomRequiredException("Room não pode ser nulo");
        }

        if (movie == null) {
            throw new MovieRequiredException("Movie não pode ser nulo");
        }

        this.showDate = showDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.basePrice = basePrice;
        this.canceled = false;

        cinemaRoom.addSession(this);
        movie.addMovieSession(this);
    }

    public void addTicket(Ticket ticket) {
        if (!tickets.contains(ticket)) {
            tickets.add(ticket);
            ticket.setMovieSession(this);
        }
    }

    public void addSeatReservation(SeatReservation reservation) {
        if (!seatReservations.contains(reservation)) {
            seatReservations.add(reservation);
            reservation.setMovieSession(this);
        }
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

        if (today.isEqual(showDate) && (now.equals(startTime) || now.isAfter(startTime)) && now.isBefore(endTime)) {
            return MovieSessionStatus.ACTIVE;
        }

        return MovieSessionStatus.FINISHED;
    }

    @Transient
    public boolean isAvailableForPurchase() {
        return !canceled && getStatus() == MovieSessionStatus.SCHEDULED;
    }
}