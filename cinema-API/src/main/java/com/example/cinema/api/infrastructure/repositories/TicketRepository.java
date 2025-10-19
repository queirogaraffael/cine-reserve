package com.example.cinema.api.infrastructure.repositories;

import com.example.cinema.api.domain.entities.MovieSession;
import com.example.cinema.api.domain.entities.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Query("SELECT COUNT(t) > 0 FROM Ticket t WHERE t.seatNumber = :seatNumber AND t.movieSession.id = :sessionId")
    boolean isSeatTaken(@Param("seatNumber") int seatNumber, @Param("sessionId") Long sessionId);

    @Query("SELECT ms.basePrice FROM Ticket t JOIN t.movieSession ms WHERE t.id = :ticketId")
    Optional<BigDecimal> findMovieSessionPriceByTicketId(@Param("ticketId") Long ticketId);

    @Query("SELECT ms FROM Ticket t JOIN t.movieSession ms WHERE t.id = :ticketId")
    Optional<MovieSession> findMovieSessionByTicketId(@Param("ticketId") Long ticketId);

}
