package com.example.cinema.api.infrastructure.persistence;

import com.example.cinema.api.domain.genre.Genre;
import com.example.cinema.api.infrastructure.persistence.projection.GenreResponseDTOProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GenreRepositoryJpa extends JpaRepository<Genre, Long> {

    Page<GenreResponseDTOProjection> findAllByActiveTrue(Pageable pageable);

    Page<GenreResponseDTOProjection> findByNameContainingIgnoreCaseAndActiveTrue(String name, Pageable pageable);

    boolean existsByName(String name);

    boolean existsByIdAndActiveTrue(Long id);

    Optional<Genre> findByIdAndActiveTrue(Long id);

    @Modifying
    @Query("UPDATE Genre g SET g.active = false WHERE g.id = :id")
    void softDelete(@Param("id") Long id);

}