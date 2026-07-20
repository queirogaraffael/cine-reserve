package com.example.cinema.api.domain.cinema;

import com.example.cinema.api.domain.cinema.exception.CinemaLocationRequiredException;
import com.example.cinema.api.domain.cinema.exception.CinemaNameRequiredException;
import com.example.cinema.api.domain.movie.MovieExhibition;
import com.example.cinema.api.domain.room.Room;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class Cinema {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String state;

    private String logoUrl;

    @OneToMany(mappedBy = "cinema", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Room> rooms = new ArrayList<>();

    @OneToMany(mappedBy = "cinema", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<MovieExhibition> exhibitions = new ArrayList<>();

    public Cinema(String name, String city, String state, String logoUrl) {
        if (name == null || name.isBlank()) {
            throw new CinemaNameRequiredException("O nome do cinema é obrigatório.");
        }
        if (city == null || city.isBlank() || state == null || state.isBlank()) {
            throw new CinemaLocationRequiredException("Cidade e estado são obrigatórios.");
        }
        this.name = name;
        this.city = city;
        this.state = state;
        this.logoUrl = logoUrl;
    }

    public void addRoom(Room room) {
        if (!rooms.contains(room)) {
            rooms.add(room);
        }
    }

    public void addExhibition(MovieExhibition exhibition) {
        if (!exhibitions.contains(exhibition)) {
            exhibitions.add(exhibition);
        }
    }
}
