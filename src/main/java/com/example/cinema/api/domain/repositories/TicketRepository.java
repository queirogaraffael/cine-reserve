package com.example.cinema.api.domain.repositories;

import com.example.cinema.api.domain.entities.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Query("SELECT COUNT(t) > 0 FROM Ticket t WHERE t.seatNumber = :seatNumber AND t.movieSession.id = :sessionId")
    boolean isSeatTaken(@Param("seatNumber") int seatNumber, @Param("sessionId") Long sessionId);

}
