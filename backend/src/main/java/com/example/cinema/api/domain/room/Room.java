package com.example.cinema.api.domain.room;

import com.example.cinema.api.domain.movie.MovieSession;
import com.example.cinema.api.domain.room.exception.RoomInvalidCapacityException;
import com.example.cinema.api.domain.room.exception.RoomInvalidNumberException;
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

    @Column(nullable = false, unique = true)
    private String number;

    private int capacity;

    @OneToMany(mappedBy = "cinemaRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter(AccessLevel.NONE)
    @ToString.Exclude
    private List<MovieSession> movieSessions = new ArrayList<>();

    public Room(String number, int capacity) {

        if (number == null || number.isBlank())
            throw new RoomInvalidNumberException("Número inválido");

        if (capacity <= 0)
            throw new RoomInvalidCapacityException("Capacidade inválida");

        this.number = number;
        this.capacity = capacity;
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

    public void changeNumber(String newNumber) {
        if (newNumber == null || newNumber.isBlank()) {
            throw new RoomInvalidNumberException("O número da sala não pode ser nulo ou vazio");
        }

        this.number = newNumber;
    }

    public void validateSeatNumber(int seatNumber) {
        int capacity = this.getCapacity();
        if (seatNumber < 1 || seatNumber > capacity)
            throw new InvalidSeatNumberException("Assento inválido: " + seatNumber);
    }

}
