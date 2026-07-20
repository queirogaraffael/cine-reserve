package com.example.cinema.api.domain.room;

import com.example.cinema.api.domain.cinema.Cinema;
import com.example.cinema.api.domain.cinema.exception.CinemaRequiredException;
import com.example.cinema.api.domain.movie.MovieSession;

import com.example.cinema.api.domain.room.exception.RoomInvalidNameException;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter(AccessLevel.NONE)
    @ToString.Exclude
    private List<Seat> seats = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cinema_id", nullable = false)
    @ToString.Exclude
    private Cinema cinema;

    @OneToMany(mappedBy = "cinemaRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter(AccessLevel.NONE)
    @ToString.Exclude
    private List<MovieSession> movieSessions = new ArrayList<>();

    public Room(String name, Cinema cinema) {
        if (cinema == null) {
            throw new CinemaRequiredException("A sala deve pertencer a um cinema.");
        }
        if (name == null || name.isBlank()) {
            throw new RoomInvalidNameException("Nome da sala inválido.");
        }
        this.name = name;
        this.cinema = cinema;
        cinema.addRoom(this);
    }

    public void addSession(MovieSession session) {
        if (!movieSessions.contains(session)) {
            movieSessions.add(session);
            session.setCinemaRoom(this);
        }
    }

    public void addSeat(Seat seat) {
        if (!seats.contains(seat)) {
            seats.add(seat);
        }
    }

    public void changeName(String newName) {
        if (newName == null || newName.isBlank()) {
            throw new RoomInvalidNameException("O nome da sala não pode ser nulo ou vazio.");
        }
        this.name = newName;
    }
}
