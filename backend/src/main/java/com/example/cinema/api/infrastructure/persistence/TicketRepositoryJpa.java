package com.example.cinema.api.infrastructure.persistence;

import com.example.cinema.api.domain.ticket.Ticket;
import com.example.cinema.api.application.dto.tickets.TicketResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TicketRepositoryJpa extends JpaRepository<Ticket, Long> {

    @Query("""
        SELECT new com.example.cinema.api.application.dto.tickets.TicketResponseDTO(
            t.id,
            t.seatNumber,
            t.movieSession.id,
            p.id
        )
        FROM Ticket t
        JOIN t.purchase p
        WHERE t.id = :ticketId
        AND p.user.id = :userId
    """)
    Optional<TicketResponseDTO> findDtoByIdAndUserId(@Param("ticketId") Long ticketId, @Param("userId") UUID userId);

    @Query("""
        SELECT new com.example.cinema.api.application.dto.tickets.TicketResponseDTO(
            t.id,
            t.seatNumber,
            t.movieSession.id,
            p.id
        )
        FROM Ticket t
        JOIN t.purchase p
        WHERE p.id = :purchaseId
        AND p.user.id = :userId
    """)
    List<TicketResponseDTO> findAllDtosByPurchaseIdAndUserId(@Param("purchaseId") Long purchaseId, @Param("userId") UUID userId);

    @Query("""
    SELECT t
    FROM Ticket t
    JOIN t.purchase p
    WHERE t.id = :ticketId
    AND p.user.id = :userId
    """)
    Optional<Ticket> findByIdAndUserId(@Param("ticketId") Long ticketId, @Param("userId") UUID userId);
}
