package com.example.cinema.api.domain.services;

import com.example.cinema.api.domain.entities.Movie;
import com.example.cinema.api.domain.entities.MovieSession;
import com.example.cinema.api.domain.entities.Room;
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
    private final MovieSessionValidator movieSessionValidator;

    public MovieSessionService(MovieSessionRepository movieSessionRepository,
                               MovieRepository movieRepository,
                               RoomRepository roomRepository,
                               SessionMapper sessionMapper, MovieSessionValidator movieSessionValidator) {
        this.movieSessionRepository = movieSessionRepository;
        this.movieRepository = movieRepository;
        this.roomRepository = roomRepository;
        this.sessionMapper = sessionMapper;
        this.movieSessionValidator = movieSessionValidator;
    }

    @Transactional
    public MovieSessionResponseDTO createSession(MovieSessionRequestDTO dto) {
        movieSessionValidator.validateSessionRequest(dto);

        Movie movie = movieRepository.findById(dto.getMovieId()).orElseThrow(() -> new ResourceNotFoundException("Filme não encontrado"));

        Room room = roomRepository.findById(dto.getRoomId()).orElseThrow(() -> new ResourceNotFoundException("Sala não encontrada"));

        movieSessionValidator.validateSessionConflicts(dto);

        MovieSession movieSession = sessionMapper.toEntity(dto);

        // associa movie e room a movieSession
        movieSession.setMovie(movie);
        movieSession.setCinemaRoom(room);

        movieSession = movieSessionRepository.save(movieSession);

        return sessionMapper.toResponseDTO(movieSession);

    }

}



