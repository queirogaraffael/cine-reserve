package com.example.cinema.api.infrastructure.persistence;

import com.example.cinema.api.domain.entities.SeatReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface SeatReservationRepositoryJpa extends JpaRepository<SeatReservation, Long> {

    @Modifying
    @Query("""
    UPDATE SeatReservation r
    SET r.status = 'EXPIRED'
    WHERE r.status = 'RESERVED'
      AND r.expiresAt < :now
""")
    void expireOldReservations(LocalDateTime now);

}
