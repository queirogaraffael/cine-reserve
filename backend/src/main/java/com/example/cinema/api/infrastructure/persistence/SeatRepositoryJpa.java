package com.example.cinema.api.infrastructure.persistence;

import com.example.cinema.api.domain.room.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepositoryJpa extends JpaRepository<Seat, Long> {

    List<Seat> findByRoomId(Long roomId);

    List<Seat> findByRoomIdAndActiveTrue(Long roomId);
}
