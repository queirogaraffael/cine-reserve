package com.example.cinema.api.infrastructure.persistence;

import com.example.cinema.api.application.dto.room.RoomResponseDTO;
import com.example.cinema.api.domain.room.Room;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomRepositoryJpa extends JpaRepository<Room, Long> {

    @Query(
            value = "SELECT new com.example.cinema.api.application.dto.room.RoomResponseDTO(r.id, r.name, r.cinema.id) FROM Room r WHERE r.active = true",
            countQuery = "SELECT count(r) FROM Room r WHERE r.active = true"
    )
    Page<RoomResponseDTO> findAllPaginado(Pageable pageable);

    @Query(
            value = "SELECT new com.example.cinema.api.application.dto.room.RoomResponseDTO(r.id, r.name, r.cinema.id) FROM Room r WHERE r.active = true AND r.cinema.id = :cinemaId",
            countQuery = "SELECT count(r) FROM Room r WHERE r.active = true AND r.cinema.id = :cinemaId"
    )
    Page<RoomResponseDTO> findAllByCinemaIdPaginado(@Param("cinemaId") Long cinemaId, Pageable pageable);

    boolean existsByNameAndCinemaIdAndActiveTrue(String name, Long cinemaId);

    boolean existsByNameAndCinemaId(String name, Long cinemaId);

    boolean existsByIdAndActiveTrue(@NonNull Long id);

    Optional<Room> findByIdAndActiveTrue(Long id);

    @Modifying
    @Query("UPDATE Room r SET r.active = false WHERE r.id = :id")
    void softDelete(@Param("id") Long id);
}
