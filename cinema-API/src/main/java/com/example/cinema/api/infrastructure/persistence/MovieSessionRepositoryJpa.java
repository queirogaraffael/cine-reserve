package com.example.cinema.api.infrastructure.persistence;

import com.example.cinema.api.domain.movie.MovieSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;

@Repository
public interface MovieSessionRepositoryJpa extends JpaRepository<MovieSession, Long> {

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

    @Query("SELECT r.capacity FROM MovieSession ms JOIN ms.cinemaRoom r WHERE ms.id = :sessionId")
    Integer findRoomCapacityByMovieSessionId(@Param("sessionId") Long sessionId);

}
