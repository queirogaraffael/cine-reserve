package com.example.cinema.api.domain.room;

import com.example.cinema.api.domain.movie.MovieSession;
import com.example.cinema.api.domain.room.exception.RoomInvalidCapacityException;
import com.example.cinema.api.domain.room.exception.RoomInvalidNumberException;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, unique = true)
    private String number;
    private int capacity;

    @OneToMany(mappedBy = "cinemaRoom", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MovieSession> movieSessions = new ArrayList<>();

    public Room(String numero, int capacidade) {

        if (numero == null || numero.isBlank()) {
            throw new RoomInvalidNumberException("O número da sala não pode ser nulo ou vazio");
        }

        if (capacidade <= 0) {
            throw new RoomInvalidCapacityException("A capacidade da sala deve ser maior que zero");
        }

        this.number = numero;
        this.capacity = capacidade;
    }

    public void addSession(MovieSession session) {
        movieSessions.add(session);
        session.setCinemaRoom(this);
    }

}
