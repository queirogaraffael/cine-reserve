package com.example.cinema.api.infrastructure.repositories;

import com.example.cinema.api.domain.entities.Genre;
import com.example.cinema.api.infrastructure.repositories.projection.GenreResponseDTOProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {

    Page<GenreResponseDTOProjection> findAllBy(Pageable pageable);

    Page<GenreResponseDTOProjection> findByNameContainingIgnoreCase(String name, Pageable pageable);

    boolean existsByName(String name);

    boolean existsById(Long id);

}