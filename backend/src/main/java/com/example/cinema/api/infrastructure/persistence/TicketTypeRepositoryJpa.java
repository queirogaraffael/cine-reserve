package com.example.cinema.api.infrastructure.persistence;

import com.example.cinema.api.domain.ticket.TicketType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketTypeRepositoryJpa extends JpaRepository<TicketType, Long> {

    List<TicketType> findByActiveTrue();
}
