package com.example.cinema.api.application.service;

import com.example.cinema.api.application.dto.movie.MovieExhibitionCardDTO;
import com.example.cinema.api.application.dto.movie.MovieExhibitionRequestDTO;
import com.example.cinema.api.application.dto.movie.MovieListingFilterDTO;
import com.example.cinema.api.domain.cinema.Cinema;
import com.example.cinema.api.domain.cinema.exception.CinemaNotFoundException;
import com.example.cinema.api.domain.movie.Movie;
import com.example.cinema.api.domain.movie.MovieExhibition;
import com.example.cinema.api.domain.movie.exception.MovieExhibitionNotFoundException;
import com.example.cinema.api.domain.movie.exception.MovieNotFoundException;
import com.example.cinema.api.infrastructure.persistence.CinemaRepositoryJpa;
import com.example.cinema.api.infrastructure.persistence.MovieExhibitionRepositoryJpa;
import com.example.cinema.api.infrastructure.persistence.MovieRepositoryJpa;
import com.example.cinema.api.infrastructure.persistence.specification.MovieExhibitionSpecification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MovieExhibitionService {

    private final MovieExhibitionRepositoryJpa movieExhibitionRepositoryJpa;
    private final MovieRepositoryJpa movieRepositoryJpa;
    private final CinemaRepositoryJpa cinemaRepositoryJpa;

    public MovieExhibitionService(MovieExhibitionRepositoryJpa movieExhibitionRepositoryJpa, MovieRepositoryJpa movieRepositoryJpa, CinemaRepositoryJpa cinemaRepositoryJpa) {
        this.movieExhibitionRepositoryJpa = movieExhibitionRepositoryJpa;
        this.movieRepositoryJpa = movieRepositoryJpa;
        this.cinemaRepositoryJpa = cinemaRepositoryJpa;
    }

    @Transactional(readOnly = true)
    public List<MovieExhibitionCardDTO> listExhibitions(Long cinemaId, MovieListingFilterDTO filter) {
        if (!cinemaRepositoryJpa.existsById(cinemaId)) {
            throw new CinemaNotFoundException("Cinema " + cinemaId + " não encontrado.");
        }
        List<MovieExhibition> exhibitions = movieExhibitionRepositoryJpa.findAll(MovieExhibitionSpecification.withFilters(cinemaId, filter));
        return exhibitions.stream()
                .map(MovieExhibitionCardDTO::new)
                .toList();
    }

    @Transactional
    public MovieExhibitionCardDTO createExhibition(MovieExhibitionRequestDTO dto) {
        Movie movie = movieRepositoryJpa.findById(dto.getMovieId())
                .orElseThrow(() -> new MovieNotFoundException("Filme " + dto.getMovieId() + " não encontrado."));
        Cinema cinema = cinemaRepositoryJpa.findById(dto.getCinemaId())
                .orElseThrow(() -> new CinemaNotFoundException("Cinema " + dto.getCinemaId() + " não encontrado."));

        MovieExhibition exhibition = new MovieExhibition(movie, cinema, dto.getFormat(), dto.getAudio());
        movieExhibitionRepositoryJpa.save(exhibition);
        return new MovieExhibitionCardDTO(exhibition);
    }

    @Transactional
    public MovieExhibitionCardDTO getExhibitionById(Long exhibitionId) {
        MovieExhibition exhibition = movieExhibitionRepositoryJpa.findById(exhibitionId)
                .orElseThrow(() -> new MovieExhibitionNotFoundException("Exibição " + exhibitionId + " não encontrada."));
        return new MovieExhibitionCardDTO(exhibition);
    }

    @Transactional
    public void activateExhibition(Long exhibitionId) {
        MovieExhibition exhibition = movieExhibitionRepositoryJpa.findById(exhibitionId)
                .orElseThrow(() -> new MovieExhibitionNotFoundException("Exibição " + exhibitionId + " não encontrada."));
        exhibition.activate();
        movieExhibitionRepositoryJpa.save(exhibition);
    }

    @Transactional
    public void deactivateExhibition(Long exhibitionId) {
        MovieExhibition exhibition = movieExhibitionRepositoryJpa.findById(exhibitionId)
                .orElseThrow(() -> new MovieExhibitionNotFoundException("Exibição " + exhibitionId + " não encontrada."));
        exhibition.deactivate();
        movieExhibitionRepositoryJpa.save(exhibition);
    }
}
