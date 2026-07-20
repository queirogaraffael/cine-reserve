package com.example.cinema.api.infrastructure.persistence;

import com.example.cinema.api.domain.seatreservation.SeatReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SeatReservationRepositoryJpa extends JpaRepository<SeatReservation, Long> {

    @Modifying
    @Query("""
    UPDATE SeatReservation r
    SET r.status = 'EXPIRED'
    WHERE r.status = 'RESERVED'
      AND r.expiresAt < :now
    """)
    void expireOldReservations(LocalDateTime now);


    @Query("""
    SELECT r FROM SeatReservation r
    WHERE r.seat.id IN :seatIds
    AND r.movieSession.id = :sessionId
    AND r.status = 'RESERVED'
    """)
    List<SeatReservation> findActiveReservationsBySeatIdsAndSession(@Param("seatIds") List<Long> seatIds,
                                                                    @Param("sessionId") Long sessionId);
}
