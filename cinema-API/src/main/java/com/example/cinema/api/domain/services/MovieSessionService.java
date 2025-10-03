package com.example.cinema.api.domain.services;

import com.example.cinema.api.domain.entities.Movie;
import com.example.cinema.api.domain.entities.MovieSession;
import com.example.cinema.api.domain.entities.Room;
import com.example.cinema.api.domain.enums.MovieSessionStatus;
import com.example.cinema.api.infrastructure.repositories.MovieRepository;
import com.example.cinema.api.infrastructure.repositories.MovieSessionRepository;
import com.example.cinema.api.infrastructure.repositories.RoomRepository;
import com.example.cinema.api.shared.dtos.movieSession.MovieSessionRequestDTO;
import com.example.cinema.api.shared.dtos.movieSession.MovieSessionResponseDTO;

import com.example.cinema.api.shared.exceptions.ResourceNotFoundException;
import com.example.cinema.api.shared.mappers.SessionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MovieSessionService {

    private final MovieSessionRepository movieSessionRepository;
    private final MovieRepository movieRepository;
    private final RoomRepository roomRepository;
    private final SessionMapper sessionMapper;

    public MovieSessionService(MovieSessionRepository movieSessionRepository,
                               MovieRepository movieRepository,
                               RoomRepository roomRepository,
                               SessionMapper sessionMapper) {
        this.movieSessionRepository = movieSessionRepository;
        this.movieRepository = movieRepository;
        this.roomRepository = roomRepository;
        this.sessionMapper = sessionMapper;
    }

    @Transactional
    public MovieSessionResponseDTO createSession(MovieSessionRequestDTO dto) {

        if (!dto.getStartTime().isBefore(dto.getEndTime())) {
            throw new IllegalArgumentException("A hora de início deve ser antes da hora de término.");
        }

        Movie movie = movieRepository.findById(dto.getMovieId()).orElseThrow(() -> new ResourceNotFoundException("Filme não encontrado"));

        Room room = roomRepository.findById(dto.getRoomId()).orElseThrow(() -> new ResourceNotFoundException("Sala não encontrada"));

        boolean conflict = movieSessionRepository.existsSessionConflict(
                dto.getRoomId(),
                dto.getShowDate(),
                dto.getStartTime(),
                dto.getEndTime()
        );

        if (conflict) {
            throw new IllegalArgumentException("A sala já está reservada para esse horário.");
        }

        MovieSession movieSession = sessionMapper.toEntity(dto);

        // associa movie e room a movieSession
        movieSession.setMovie(movie);
        movieSession.setCinemaRoom(room);

        movieSession = movieSessionRepository.save(movieSession);

        return sessionMapper.toResponseDTO(movieSession);

    }

}



