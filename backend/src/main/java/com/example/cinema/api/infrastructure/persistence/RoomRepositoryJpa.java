package com.example.cinema.api.infrastructure.persistence;

import com.example.cinema.api.application.dto.room.RoomResponseDTO;
import com.example.cinema.api.domain.room.Room;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepositoryJpa extends JpaRepository<Room, Long> {

    @Query(
            value = "SELECT new com.example.cinema.api.application.dto.room.RoomResponseDTO(r.id, r.name, r.capacity, r.cinema.id) FROM Room r",
            countQuery = "SELECT count(r) FROM Room r"
    )
    Page<RoomResponseDTO> findAllPaginado(Pageable pageable);

    boolean existsByNameAndCinemaId(String name, Long cinemaId);

    boolean existsById(@NonNull Long id);
}
