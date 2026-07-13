package com.example.cinema.api.application.service;

import com.example.cinema.api.application.dto.movie.MovieDetailsDTO;
import com.example.cinema.api.application.dto.movieSession.DaySessionsDTO;
import com.example.cinema.api.application.dto.movieSession.ExhibitionSessionsResponseDTO;
import com.example.cinema.api.application.dto.movieSession.RoomSessionsDTO;
import com.example.cinema.api.application.dto.movieSession.SessionSlotDTO;
import com.example.cinema.api.domain.movie.exception.MovieSessionNotFoundException;
import com.example.cinema.api.domain.movie.exception.MovieExhibitionNotFoundException;
import com.example.cinema.api.domain.room.exception.RoomNotFoundException;
import com.example.cinema.api.domain.room.exception.RoomScheduleConflictException;
import com.example.cinema.api.domain.movie.exception.InvalidMovieSessionTimeRangeException;
import com.example.cinema.api.domain.movie.MovieExhibition;
import com.example.cinema.api.domain.movie.MovieSession;
import com.example.cinema.api.domain.room.Room;
import com.example.cinema.api.domain.ticket.Ticket;
import com.example.cinema.api.domain.ticket.exception.TicketNotFoundException;
import com.example.cinema.api.infrastructure.persistence.MovieExhibitionRepositoryJpa;
import com.example.cinema.api.infrastructure.persistence.MovieSessionRepositoryJpa;
import com.example.cinema.api.infrastructure.persistence.RoomRepositoryJpa;
import com.example.cinema.api.application.dto.movieSession.MovieSessionRequestDTO;
import com.example.cinema.api.application.dto.movieSession.MovieSessionResponseDTO;
import com.example.cinema.api.infrastructure.persistence.TicketRepositoryJpa;
import com.example.cinema.api.application.mapper.SessionMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class MovieSessionService {

        @Value("${cinema.session.availability-cutoff-minutes:60}")
        private int cutoffMinutes;

        private final MovieSessionRepositoryJpa movieSessionRepositoryJpa;
        private final MovieExhibitionRepositoryJpa movieExhibitionRepositoryJpa;
        private final RoomRepositoryJpa roomRepositoryJpa;
        private final TicketRepositoryJpa ticketRepositoryJpa;
        private final SessionMapper sessionMapper;

        public MovieSessionService(MovieSessionRepositoryJpa movieSessionRepositoryJpa,
                        MovieExhibitionRepositoryJpa movieExhibitionRepositoryJpa, RoomRepositoryJpa roomRepositoryJpa,
                        TicketRepositoryJpa ticketRepositoryJpa, SessionMapper sessionMapper) {
                this.movieSessionRepositoryJpa = movieSessionRepositoryJpa;
                this.movieExhibitionRepositoryJpa = movieExhibitionRepositoryJpa;
                this.roomRepositoryJpa = roomRepositoryJpa;
                this.ticketRepositoryJpa = ticketRepositoryJpa;
                this.sessionMapper = sessionMapper;
        }

        @Transactional
        public MovieSessionResponseDTO createSession(MovieSessionRequestDTO dto) {

                if (!dto.getStartTime().isBefore(dto.getEndTime())) {
                        throw new InvalidMovieSessionTimeRangeException(
                                        "A hora de início deve ser antes da hora de término.");
                }

                boolean conflict = movieSessionRepositoryJpa.existsSessionConflict(dto.getRoomId(), dto.getShowDate(),
                                dto.getStartTime(), dto.getEndTime());

                if (conflict) {
                        throw new RoomScheduleConflictException("A sala já está reservada para esse horário.");
                }

                MovieExhibition exhibition = movieExhibitionRepositoryJpa.findById(dto.getExhibitionId())
                                .orElseThrow(() -> new MovieExhibitionNotFoundException(
                                                "Exibição: " + dto.getExhibitionId() + " não encontrada"));

                Room room = roomRepositoryJpa.findById(dto.getRoomId()).orElseThrow(
                                () -> new RoomNotFoundException("Sala: " + dto.getRoomId() + "não encontrada"));

                MovieSession movieSession = new MovieSession(dto.getShowDate(), dto.getStartTime(), dto.getEndTime(),
                                dto.getBasePrice(), room, exhibition);

                movieSession = movieSessionRepositoryJpa.save(movieSession);

                return sessionMapper.toResponseDTO(movieSession);
        }

        @Transactional(readOnly = true)
        public MovieSessionResponseDTO getMovieSessionById(Long movieSessionId) {

                MovieSession movieSession = movieSessionRepositoryJpa.findById(movieSessionId)
                                .orElseThrow(() -> new MovieSessionNotFoundException(
                                                "MovieSession: " + movieSessionId + " não encontrada."));

                return sessionMapper.toResponseDTO(movieSession);
        }

        @Transactional(readOnly = true)
        public List<Integer> getAvailableSeats(Long sessionId) {

                Integer capacity = movieSessionRepositoryJpa.findRoomCapacityByMovieSessionId(sessionId);

                if (capacity == null) {
                        throw new MovieSessionNotFoundException("Sessão " + sessionId + " não encontrada.");
                }

                List<Integer> unavailable = movieSessionRepositoryJpa.findUnavailableSeatNumbers(sessionId);

                Set<Integer> unavailableSet = new HashSet<>(unavailable);

                return IntStream.rangeClosed(1, capacity)
                                .filter(seat -> !unavailableSet.contains(seat))
                                .boxed()
                                .toList();
        }

        @Transactional
        public MovieSessionResponseDTO getMovieSessionByTicketId(Long ticketId, UUID userId) {

                Ticket ticket = ticketRepositoryJpa.findByIdAndUserId(ticketId, userId)
                                .orElseThrow(() -> new TicketNotFoundException(
                                                "Ticket: " + ticketId + " não encontrado."));

                return movieSessionRepositoryJpa.findMovieSessionByTicketId(ticket.getId())
                                .orElseThrow(() -> new MovieSessionNotFoundException(
                                                "MovieSession para Ticket: " + ticketId + " não encontrada."));
        }

        @Transactional(readOnly = true)
        public ExhibitionSessionsResponseDTO getSessionsByExhibition(Long exhibitionId) {
                MovieExhibition exhibition = movieExhibitionRepositoryJpa.findById(exhibitionId)
                                .orElseThrow(() -> new MovieExhibitionNotFoundException(
                                                "Exibição: " + exhibitionId + " não encontrada."));

                LocalDate startDate = LocalDate.now();
                LocalDate endDate = startDate.plusDays(6);
                LocalDateTime cutoff = LocalDateTime.now().plusMinutes(cutoffMinutes);

                List<MovieSession> sessions = movieSessionRepositoryJpa
                                .findSessionsByExhibitionAndDateRange(exhibitionId, startDate, endDate);

                Map<LocalDate, Map<Room, List<MovieSession>>> grouped = sessions.stream()
                                .collect(Collectors.groupingBy(
                                                MovieSession::getShowDate,
                                                Collectors.groupingBy(MovieSession::getCinemaRoom)));

                List<DaySessionsDTO> days = grouped.entrySet().stream()
                                .sorted(Map.Entry.comparingByKey())
                                .map(dayEntry -> {
                                        List<RoomSessionsDTO> rooms = dayEntry.getValue().entrySet().stream()
                                                        .sorted(Comparator.comparing(e -> e.getKey().getName()))
                                                        .map(roomEntry -> {
                                                                List<SessionSlotDTO> slots = roomEntry.getValue()
                                                                                .stream()
                                                                                .sorted(Comparator.comparing(
                                                                                                MovieSession::getStartTime))
                                                                                .map(session -> {
                                                                                        LocalDateTime sessionStart = LocalDateTime
                                                                                                        .of(
                                                                                                                        session.getShowDate(),
                                                                                                                        session.getStartTime());
                                                                                        String status = sessionStart
                                                                                                        .isAfter(cutoff)
                                                                                                                        ? "AVAILABLE"
                                                                                                                        : "UNAVAILABLE";
                                                                                        return new SessionSlotDTO(
                                                                                                        session.getId(),
                                                                                                        session.getStartTime(),
                                                                                                        status);
                                                                                })
                                                                                .toList();
                                                                return new RoomSessionsDTO(
                                                                                roomEntry.getKey().getId(),
                                                                                roomEntry.getKey().getName(),
                                                                                slots);
                                                        })
                                                        .toList();
                                        String dayOfWeek = dayEntry.getKey()
                                                        .getDayOfWeek()
                                                        .getDisplayName(
                                                                        java.time.format.TextStyle.FULL,
                                                                        new java.util.Locale("pt", "BR"))
                                                        .toUpperCase();
                                        return new DaySessionsDTO(dayEntry.getKey(), dayOfWeek, rooms);
                                })
                                .toList();

                return new ExhibitionSessionsResponseDTO(
                                exhibition.getId(),
                                exhibition.getFormat(),
                                exhibition.getAudio(),
                                new MovieDetailsDTO(exhibition.getMovie()),
                                days);
        }

}
