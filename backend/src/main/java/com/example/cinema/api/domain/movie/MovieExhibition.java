package com.example.cinema.api.domain.movie;

import com.example.cinema.api.domain.cinema.Cinema;
import com.example.cinema.api.domain.movie.exception.MovieExhibitionDataRequiredException;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"movie_id", "cinema_id", "format", "audio"})
})
@Entity
public class MovieExhibition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    @ToString.Exclude
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cinema_id", nullable = false)
    @ToString.Exclude
    private Cinema cinema;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MovieFormat format;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AudioType audio;

    @Column(nullable = false)
    private boolean active = true;

    @OneToMany(mappedBy = "movieExhibition", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<MovieSession> sessions = new ArrayList<>();

    public MovieExhibition(Movie movie, Cinema cinema, MovieFormat format, AudioType audio) {
        if (movie == null || cinema == null || format == null || audio == null) {
            throw new MovieExhibitionDataRequiredException("Filme, cinema, formato e áudio são obrigatórios para criar uma exibição.");
        }
        this.movie = movie;
        this.cinema = cinema;
        this.format = format;
        this.audio = audio;
        this.active = true;
        movie.addExhibition(this);
        cinema.addExhibition(this);
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public void addSession(MovieSession session) {
        if (!sessions.contains(session)) {
            sessions.add(session);
        }
    }
}
