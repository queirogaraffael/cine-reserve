package com.example.cinema.api.infrastructure.persistence;

import com.example.cinema.api.domain.ticket.Ticket;
import com.example.cinema.api.domain.user.User;
import com.example.cinema.api.shared.dtos.tickets.TicketResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepositoryJpa extends JpaRepository<Ticket, Long> {

    @Query("""
        SELECT new com.example.cinema.api.shared.dtos.tickets.TicketResponseDTO(
            t.id,
            t.seatNumber,
            t.movieSession.id,
            p.id
        )
        FROM Ticket t
        JOIN t.purchase p
        WHERE t.id = :ticketId
        AND p.user = :user
    """)
    Optional<TicketResponseDTO> findDtoByIdAndUser(@Param("ticketId") Long ticketId, @Param("user") User user);

    @Query("""
        SELECT new com.example.cinema.api.shared.dtos.tickets.TicketResponseDTO(
            t.id,
            t.seatNumber,
            t.movieSession.id,
            p.id
        )
        FROM Ticket t
        JOIN t.purchase p
        WHERE p.id = :purchaseId
        AND p.user = :user
    """)
    List<TicketResponseDTO> findAllDtosByPurchaseIdAndUser(@Param("purchaseId") Long purchaseId, @Param("user") User user);
}
