package com.example.cinema.api.infrastructure.repositories;

import com.example.cinema.api.shared.dtos.room.RoomResponseDTO;
import com.example.cinema.api.domain.entities.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query(
            value = "SELECT new com.example.cinema.api.shared.dtos.room.RoomResponseDTO(r.id, r.number, r.capacity) FROM Room r",
            countQuery = "SELECT count(r) FROM Room r"
    )
    Page<RoomResponseDTO> findAllPaginado(Pageable pageable);

    boolean existsByNumber(String number);

    boolean existsById(Long id);
}
