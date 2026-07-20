package com.example.cinema.api.infrastructure.persistence;

import com.example.cinema.api.domain.movie.MovieSession;
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

    @Query("SELECT r.seat.id FROM SeatReservation r WHERE r.movieSession.id = :sessionId AND r.status IN ('RESERVED', 'CONSUMED')")
    List<Long> findUnavailableSeatIds(@Param("sessionId") Long sessionId);

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



    @Query("SELECT ms FROM MovieSession ms JOIN FETCH ms.cinemaRoom WHERE ms.id = :sessionId")
    Optional<MovieSession> findByIdWithRoom(@Param("id") Long sessionId);

    @Query("""
        SELECT s FROM MovieSession s
        JOIN FETCH s.cinemaRoom r
        JOIN FETCH s.movieExhibition e
        JOIN FETCH e.movie m
        JOIN FETCH m.genre g
        WHERE e.id = :exhibitionId
          AND s.showDate BETWEEN :startDate AND :endDate
          AND s.canceled = false
        ORDER BY s.showDate ASC, r.name ASC, s.startTime ASC
    """)
    List<MovieSession> findSessionsByExhibitionAndDateRange(
            @Param("exhibitionId") Long exhibitionId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
