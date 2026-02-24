package com.example.cinema.api.application.service;

import com.example.cinema.api.domain.movie.MovieSession;
import com.example.cinema.api.domain.seatreservation.SeatReservation;
import com.example.cinema.api.domain.user.User;
import com.example.cinema.api.domain.seatreservation.ReservationStatus;
import com.example.cinema.api.infrastructure.persistence.MovieSessionRepositoryJpa;
import com.example.cinema.api.infrastructure.persistence.SeatReservationRepositoryJpa;
import com.example.cinema.api.application.dto.seatreservation.SeatReservationRequestDTO;
import com.example.cinema.api.application.dto.seatreservation.SeatReservationResponseDTO;
import com.example.cinema.api.shared.exceptions.ResourceNotFoundException;
import com.example.cinema.api.domain.ticket.exception.SeatAlreadyReservedException;
import com.example.cinema.api.application.mapper.SeatReservationMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ReservaService {

    private final MovieSessionRepositoryJpa movieSessionRepositoryJpa;
    private final SeatReservationRepositoryJpa seatReservationRepositoryJpa;
    private final SeatReservationMapper seatReservationMapper;
    private final UserService userService;

    public ReservaService(MovieSessionRepositoryJpa movieSessionRepositoryJpa, SeatReservationRepositoryJpa seatReservationRepositoryJpa, SeatReservationMapper seatReservationMapper, UserService userService) {
        this.movieSessionRepositoryJpa = movieSessionRepositoryJpa;
        this.seatReservationRepositoryJpa = seatReservationRepositoryJpa;
        this.seatReservationMapper = seatReservationMapper;
        this.userService = userService;
    }

    @Transactional
    public SeatReservationResponseDTO criarReserva(Long movieSessionId, SeatReservationRequestDTO dto) {

        MovieSession movieSession = movieSessionRepositoryJpa.findById(movieSessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Sessão de filme não encontrada"));

        Integer roomCapacity =
                movieSessionRepositoryJpa.findRoomCapacityByMovieSessionId(movieSessionId);

        int seatNumber = dto.getSeatNumber();

        if (seatNumber < 1 || seatNumber > roomCapacity) {
            throw new IllegalArgumentException("Assento inválido");
        }

        User user = userService.getAuthenticatedUser();

        SeatReservation reservation = seatReservationMapper.toEntity(dto);
        reservation.setMovieSession(movieSession);
        reservation.setUser(user);
        reservation.setStatus(ReservationStatus.RESERVED);
        reservation.setExpiresAt(LocalDateTime.now().plusMinutes(10));

        try {
            SeatReservation savedSeatReservation = seatReservationRepositoryJpa.save(reservation);
            return seatReservationMapper.toResponseDTO(savedSeatReservation);

        } catch (DataIntegrityViolationException ex) {
            throw new SeatAlreadyReservedException("Assento já reservado para essa sessão");
        }

    }

    @Transactional
    public void cancelarReserva(Long idReserva){

        User user = userService.getAuthenticatedUser();

        int updated = seatReservationRepositoryJpa.cancelReservation(idReserva, user);

        if (updated == 0) {
            throw new ResourceNotFoundException("Reserva não encontrada/não pode ser cancelada");
        }
    }

}
