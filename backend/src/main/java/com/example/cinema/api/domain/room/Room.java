package com.example.cinema.api.domain.room;

import com.example.cinema.api.domain.cinema.Cinema;
import com.example.cinema.api.domain.cinema.exception.CinemaRequiredException;
import com.example.cinema.api.domain.movie.MovieSession;
import com.example.cinema.api.domain.room.exception.RoomInvalidCapacityException;
import com.example.cinema.api.domain.room.exception.RoomInvalidNameException;
import com.example.cinema.api.domain.seatreservation.exception.InvalidSeatNumberException;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private String name;

    private int capacity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cinema_id", nullable = false)
    @ToString.Exclude
    private Cinema cinema;

    @OneToMany(mappedBy = "cinemaRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter(AccessLevel.NONE)
    @ToString.Exclude
    private List<MovieSession> movieSessions = new ArrayList<>();

    public Room(String name, int capacity, Cinema cinema) {
        if (cinema == null) {
            throw new CinemaRequiredException("A sala deve pertencer a um cinema.");
        }
        if (name == null || name.isBlank()) {
            throw new RoomInvalidNameException("Nome da sala inválido.");
        }
        if (capacity <= 0) {
            throw new RoomInvalidCapacityException("Capacidade inválida");
        }
        this.name = name;
        this.capacity = capacity;
        this.cinema = cinema;
        cinema.addRoom(this);
    }

    public void addSession(MovieSession session) {
        if (!movieSessions.contains(session)) {
            movieSessions.add(session);
            session.setCinemaRoom(this);
        }
    }

    public void changeCapacity(int newCapacity) {
        if (newCapacity <= 0) {
            throw new RoomInvalidCapacityException("A capacidade da sala deve ser maior que zero");
        }
        this.capacity = newCapacity;
    }

    public void changeName(String newName) {
        if (newName == null || newName.isBlank()) {
            throw new RoomInvalidNameException("O nome da sala não pode ser nulo ou vazio.");
        }
        this.name = newName;
    }

    public void validateSeatNumber(int seatNumber) {
        if (seatNumber < 1 || seatNumber > this.capacity) {
            throw new InvalidSeatNumberException("Assento inválido: " + seatNumber);
        }
    }
}
