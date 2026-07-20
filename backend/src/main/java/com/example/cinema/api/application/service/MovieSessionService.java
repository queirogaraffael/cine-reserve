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
import com.example.cinema.api.infrastructure.persistence.MovieExhibitionRepositoryJpa;
import com.example.cinema.api.infrastructure.persistence.MovieSessionRepositoryJpa;
import com.example.cinema.api.infrastructure.persistence.RoomRepositoryJpa;
import com.example.cinema.api.infrastructure.persistence.SeatRepositoryJpa;
import com.example.cinema.api.infrastructure.persistence.TicketTypeRepositoryJpa;
import com.example.cinema.api.infrastructure.persistence.PromotionRepositoryJpa;
import com.example.cinema.api.domain.ticket.TicketType;
import com.example.cinema.api.domain.promotion.Promotion;
import com.example.cinema.api.application.dto.movieSession.MovieSessionRequestDTO;
import com.example.cinema.api.application.dto.movieSession.MovieSessionResponseDTO;
import com.example.cinema.api.application.dto.seat.SeatDTO;
import com.example.cinema.api.application.dto.ticket.TicketTypeDTO;
import com.example.cinema.api.domain.room.Seat;
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
        private final SeatRepositoryJpa seatRepositoryJpa;
        private final TicketTypeRepositoryJpa ticketTypeRepository;
        private final PromotionRepositoryJpa promotionRepository;

        private final SessionMapper sessionMapper;

        public MovieSessionService(MovieSessionRepositoryJpa movieSessionRepositoryJpa,
                        MovieExhibitionRepositoryJpa movieExhibitionRepositoryJpa, RoomRepositoryJpa roomRepositoryJpa,
                        SeatRepositoryJpa seatRepositoryJpa,
                        TicketTypeRepositoryJpa ticketTypeRepository,
                        PromotionRepositoryJpa promotionRepository,
                        SessionMapper sessionMapper) {
                this.movieSessionRepositoryJpa = movieSessionRepositoryJpa;
                this.movieExhibitionRepositoryJpa = movieExhibitionRepositoryJpa;
                this.roomRepositoryJpa = roomRepositoryJpa;
                this.seatRepositoryJpa = seatRepositoryJpa;
                this.ticketTypeRepository = ticketTypeRepository;
                this.promotionRepository = promotionRepository;
                this.sessionMapper = sessionMapper;
        }

        @Transactional
        public MovieSessionResponseDTO createSession(MovieSessionRequestDTO dto) {

                if (!dto.getStartTime().isBefore(dto.getEndTime())) {
                        throw new InvalidMovieSessionTimeRangeException(
                                        "A hora de inÃ­cio deve ser antes da hora de tÃ©rmino.");
                }

                boolean conflict = movieSessionRepositoryJpa.existsSessionConflict(dto.getRoomId(), dto.getShowDate(),
                                dto.getStartTime(), dto.getEndTime());

                if (conflict) {
                        throw new RoomScheduleConflictException("A sala jÃ¡ estÃ¡ reservada para esse horÃ¡rio.");
                }

                MovieExhibition exhibition = movieExhibitionRepositoryJpa.findById(dto.getExhibitionId())
                                .orElseThrow(() -> new MovieExhibitionNotFoundException(
                                                "ExibiÃ§Ã£o: " + dto.getExhibitionId() + " nÃ£o encontrada"));

                Room room = roomRepositoryJpa.findById(dto.getRoomId()).orElseThrow(
                                () -> new RoomNotFoundException("Sala: " + dto.getRoomId() + "nÃ£o encontrada"));

                MovieSession movieSession = new MovieSession(dto.getShowDate(), dto.getStartTime(), dto.getEndTime(),
                                dto.getBasePrice(), room, exhibition);

                movieSession = movieSessionRepositoryJpa.save(movieSession);

                return sessionMapper.toResponseDTO(movieSession);
        }

        @Transactional(readOnly = true)
        public MovieSessionResponseDTO getMovieSessionById(Long movieSessionId) {

                MovieSession movieSession = movieSessionRepositoryJpa.findById(movieSessionId)
                                .orElseThrow(() -> new MovieSessionNotFoundException(
                                                "MovieSession: " + movieSessionId + " nÃ£o encontrada."));

                return sessionMapper.toResponseDTO(movieSession);
        }

        @Transactional(readOnly = true)
        public List<SeatDTO> getAvailableSeats(Long sessionId) {

                MovieSession movieSession = movieSessionRepositoryJpa.findById(sessionId)
                                .orElseThrow(() -> new MovieSessionNotFoundException("SessÃ£o " + sessionId + " nÃ£o encontrada."));

                Room room = movieSession.getCinemaRoom();

                List<Seat> allSeats = seatRepositoryJpa.findByRoomIdAndActiveTrue(room.getId());
                List<Long> unavailableSeatIds = movieSessionRepositoryJpa.findUnavailableSeatIds(sessionId);
                Set<Long> unavailableSet = new HashSet<>(unavailableSeatIds);

                return allSeats.stream()
                                .map(seat -> new SeatDTO(
                                                seat.getId(),
                                                seat.getCode(),
                                                seat.getRowLetter(),
                                                seat.getColumnNumber(),
                                                seat.getType().name(),
                                                !unavailableSet.contains(seat.getId())
                                ))
                                .toList();
        }



        @Transactional(readOnly = true)
        public ExhibitionSessionsResponseDTO getSessionsByExhibition(Long exhibitionId) {
                MovieExhibition exhibition = movieExhibitionRepositoryJpa.findById(exhibitionId)
                                .orElseThrow(() -> new MovieExhibitionNotFoundException(
                                                "ExibiÃ§Ã£o: " + exhibitionId + " nÃ£o encontrada."));

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

        @Transactional(readOnly = true)
        public List<TicketTypeDTO> getTicketTypesWithPromotions(Long sessionId) {
            MovieSession session = movieSessionRepositoryJpa.findById(sessionId)
                    .orElseThrow(() -> new MovieSessionNotFoundException("Sessão não encontrada."));

            List<TicketType> tickets = ticketTypeRepository.findAll();
            List<Promotion> activePromotions = promotionRepository.findByCinemaIdAndActiveTrue(session.getCinemaRoom().getCinema().getId());
            Promotion appliedPromotion = activePromotions.stream().filter(p -> p.appliesTo(session)).findFirst().orElse(null);

            return tickets.stream().map(ticket -> {
                java.math.BigDecimal finalPrice = ticket.getPrice();
                if (appliedPromotion != null) {
                    if (appliedPromotion.getFixedPrice() != null) {
                        finalPrice = appliedPromotion.getFixedPrice();
                    } else if (appliedPromotion.getDiscountPercentage() != null) {
                        finalPrice = finalPrice.multiply(java.math.BigDecimal.ONE.subtract(appliedPromotion.getDiscountPercentage().divide(java.math.BigDecimal.valueOf(100))));
                    }
                }
                return new TicketTypeDTO(
                        ticket.getId(),
                        ticket.getName(),
                        ticket.getDescription(),
                        ticket.getCategory().name(),
                        finalPrice,
                        ticket.getPrice()
                );
            }).toList();
        }
}
