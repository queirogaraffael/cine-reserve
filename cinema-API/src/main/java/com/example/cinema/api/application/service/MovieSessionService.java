package com.example.cinema.api.application.service;

import com.example.cinema.api.domain.movie.Movie;
import com.example.cinema.api.domain.movie.MovieSession;
import com.example.cinema.api.domain.room.Room;
import com.example.cinema.api.infrastructure.persistence.MovieRepositoryJpa;
import com.example.cinema.api.infrastructure.persistence.MovieSessionRepositoryJpa;
import com.example.cinema.api.infrastructure.persistence.RoomRepositoryJpa;
import com.example.cinema.api.application.dto.movieSession.MovieSessionRequestDTO;
import com.example.cinema.api.application.dto.movieSession.MovieSessionResponseDTO;

import com.example.cinema.api.shared.exception.ResourceNotFoundException;
import com.example.cinema.api.application.mapper.SessionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MovieSessionService {

    private final MovieSessionRepositoryJpa movieSessionRepositoryJpa;
    private final MovieRepositoryJpa movieRepositoryJpa;
    private final RoomRepositoryJpa roomRepositoryJpa;
    private final SessionMapper sessionMapper;

    public MovieSessionService(MovieSessionRepositoryJpa movieSessionRepositoryJpa,
                               MovieRepositoryJpa movieRepositoryJpa,
                               RoomRepositoryJpa roomRepositoryJpa,
                               SessionMapper sessionMapper) {
        this.movieSessionRepositoryJpa = movieSessionRepositoryJpa;
        this.movieRepositoryJpa = movieRepositoryJpa;
        this.roomRepositoryJpa = roomRepositoryJpa;
        this.sessionMapper = sessionMapper;
    }

    @Transactional
    public MovieSessionResponseDTO createSession(MovieSessionRequestDTO dto) {

        if (!dto.getStartTime().isBefore(dto.getEndTime())) {
            throw new IllegalArgumentException("A hora de início deve ser antes da hora de término.");
        }

        Movie movie = movieRepositoryJpa.findById(dto.getMovieId()).orElseThrow(() -> new ResourceNotFoundException("Filme não encontrado"));

        Room room = roomRepositoryJpa.findById(dto.getRoomId()).orElseThrow(() -> new ResourceNotFoundException("Sala não encontrada"));

        boolean conflict = movieSessionRepositoryJpa.existsSessionConflict(
                dto.getRoomId(),
                dto.getShowDate(),
                dto.getStartTime(),
                dto.getEndTime()
        );

        if (conflict) {
            throw new IllegalArgumentException("A sala já está reservada para esse horário.");
        }

        MovieSession movieSession = sessionMapper.toEntity(dto);

        movieSession.setMovie(movie);
        movieSession.setCinemaRoom(room);

        movieSession = movieSessionRepositoryJpa.save(movieSession);

        return sessionMapper.toResponseDTO(movieSession);

    }

}



