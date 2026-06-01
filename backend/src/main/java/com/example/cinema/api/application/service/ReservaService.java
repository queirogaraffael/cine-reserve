package com.example.cinema.api.application.service;

import com.example.cinema.api.domain.movie.exception.MovieSessionNotAvailableException;
import com.example.cinema.api.domain.movie.exception.MovieSessionNotFoundException;
import com.example.cinema.api.domain.room.Room;
import com.example.cinema.api.domain.movie.MovieSession;
import com.example.cinema.api.domain.seatreservation.SeatReservation;
import com.example.cinema.api.domain.seatreservation.exception.ReservationNotFoundException;
import com.example.cinema.api.domain.user.User;
import com.example.cinema.api.infrastructure.persistence.MovieSessionRepositoryJpa;
import com.example.cinema.api.infrastructure.persistence.SeatReservationRepositoryJpa;
import com.example.cinema.api.application.dto.seatreservation.SeatReservationRequestDTO;
import com.example.cinema.api.application.dto.seatreservation.SeatReservationResponseDTO;
import com.example.cinema.api.domain.seatreservation.exception.SeatNumberAlreadyReservedException;
import com.example.cinema.api.application.mapper.SeatReservationMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

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
    public SeatReservationResponseDTO criarReserva(Long movieSessionId, SeatReservationRequestDTO dto, UUID userId) {

        User user = userService.findById(userId);

        MovieSession movieSession = movieSessionRepositoryJpa.findByIdWithRoom(movieSessionId)
                .orElseThrow(() -> new MovieSessionNotFoundException("Sessão de filme não encontrada"));

        if (!movieSession.isAvailableForPurchase()) {
            throw new MovieSessionNotAvailableException("Sessão não disponível para reserva.");
        }

        Room room = movieSession.getCinemaRoom();

        int seatNumber = dto.getSeatNumber();

        room.validateSeatNumber(seatNumber);

        if (movieSessionRepositoryJpa.isSeatUnavailable(seatNumber, movieSessionId)) {
            throw new SeatNumberAlreadyReservedException("Assento já está reservado.");
        }

        SeatReservation reservation = new SeatReservation(movieSession, seatNumber, user);

        SeatReservation savedSeatReservation = seatReservationRepositoryJpa.save(reservation);
        return seatReservationMapper.toResponseDTO(savedSeatReservation);
    }

    @Transactional
    public void cancelarReservaDeUsuario(Long idReserva, UUID userId) {

        SeatReservation reservation = seatReservationRepositoryJpa.findByIdAndUserId(idReserva, userId)
                .orElseThrow(() -> new ReservationNotFoundException("Reserva do usuário: " + userId + " não encontrada"));

        reservation.cancel();
    }

}
