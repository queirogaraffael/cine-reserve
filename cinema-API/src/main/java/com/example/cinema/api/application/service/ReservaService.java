package com.example.cinema.api.application.service;

import com.example.cinema.api.application.exception.InvalidSeatNumberException;
import com.example.cinema.api.domain.movie.MovieSession;
import com.example.cinema.api.domain.seatreservation.SeatReservation;
import com.example.cinema.api.domain.user.User;
import com.example.cinema.api.infrastructure.persistence.MovieSessionRepositoryJpa;
import com.example.cinema.api.infrastructure.persistence.SeatReservationRepositoryJpa;
import com.example.cinema.api.application.dto.seatreservation.SeatReservationRequestDTO;
import com.example.cinema.api.application.dto.seatreservation.SeatReservationResponseDTO;
import com.example.cinema.api.shared.exception.ResourceNotFound;
import com.example.cinema.api.SeatAlreadyReservedException;
import com.example.cinema.api.application.mapper.SeatReservationMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                .orElseThrow(() -> new ResourceNotFound("Sessão de filme não encontrada"));

        Integer roomCapacity = movieSessionRepositoryJpa.findRoomCapacityByMovieSessionId(movieSessionId);

        int seatNumber = dto.getSeatNumber();

        if (seatNumber < 1 || seatNumber > roomCapacity) {
            throw new InvalidSeatNumberException("Assento inválido");
        }

        if (movieSessionRepositoryJpa.isSeatUnavailable(seatNumber, movieSessionId)) {
            throw new SeatAlreadyReservedException("Assento já está reservado.");
        }

        User user = userService.getAuthenticatedUser();

        SeatReservation reservation = new SeatReservation(movieSession, dto.getSeatNumber(), user);

        try {
            SeatReservation savedSeatReservation = seatReservationRepositoryJpa.save(reservation);
            return seatReservationMapper.toResponseDTO(savedSeatReservation);

        } catch (DataIntegrityViolationException ex) {
            throw new SeatAlreadyReservedException("Assento já reservado para essa sessão");
        }

    }

    @Transactional
    public void cancelarReservaDeUsuario(Long idReserva) {

        User user = userService.getAuthenticatedUser();

        SeatReservation reservation = seatReservationRepositoryJpa.findByIdAndUser(idReserva, user).orElseThrow(() ->
                        new ResourceNotFound("Reserva de usurio: " + user.getId() +"não encontrada"));

        reservation.cancel();
    }

}
