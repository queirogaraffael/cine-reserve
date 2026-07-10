package com.example.cinema.api.infrastructure.persistence;

import com.example.cinema.api.domain.movie.MovieSession;
import com.example.cinema.api.application.dto.movieSession.MovieSessionResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MovieSessionRepositoryJpa extends JpaRepository<MovieSession, Long> {

    @Query(value = """
    SELECT EXISTS (
        SELECT 1 FROM ticket t
        WHERE t.session_id = :sessionId
        AND t.seat_number = :seatNumber
    )
    OR EXISTS (
        SELECT 1 FROM seat_reservations r
        WHERE r.session_id = :sessionId
        AND r.seat_number = :seatNumber
        AND r.status IN ('RESERVED', 'CONSUMED')
    )
    """, nativeQuery = true)
    boolean isSeatUnavailable(int seatNumber, Long sessionId);

    @Query(value = """
    SELECT seat_number FROM tickets
    WHERE session_id = :sessionId

    UNION

    SELECT seat_number FROM seat_reservations
    WHERE session_id = :sessionId
    AND status IN ('RESERVED', 'CONSUMED')
    """, nativeQuery = true)
    List<Integer> findUnavailableSeatNumbers(Long sessionId);

    @Query("""
                SELECT COUNT(ms) > 0
                FROM MovieSession ms
                WHERE ms.cinemaRoom.id = :roomId
                  AND ms.showDate = :showDate
                  AND ms.startTime < :endTime
                  AND ms.endTime > :startTime
                  AND ms.canceled = false
            """)
    boolean existsSessionConflict(
            @Param("roomId") Long roomId,
            @Param("showDate") LocalDate showDate,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );

    @Query("""
    SELECT new com.example.cinema.api.application.dto.movieSession.MovieSessionResponseDTO(
        ms.id,
        ms.showDate,
        ms.startTime,
        ms.endTime,
        ms.basePrice,
        ms.canceled,
        ms.cinemaRoom.id,
        ms.movieExhibition.id
    )
    FROM MovieSession ms
    JOIN ms.tickets t
    WHERE t.id = :ticketId
    """)
    Optional<MovieSessionResponseDTO> findMovieSessionByTicketId(@Param("ticketId") Long ticketId);

    @Query("SELECT r.capacity FROM MovieSession ms JOIN ms.cinemaRoom r WHERE ms.id = :sessionId")
    Integer findRoomCapacityByMovieSessionId(@Param("sessionId") Long sessionId);

    @Query("SELECT ms FROM MovieSession ms JOIN FETCH ms.cinemaRoom WHERE ms.id = :sessionId")
    Optional<MovieSession> findByIdWithRoom(@Param("id") Long sessionId);
}
