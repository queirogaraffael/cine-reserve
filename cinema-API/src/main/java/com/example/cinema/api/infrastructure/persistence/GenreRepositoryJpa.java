package com.example.cinema.api.infrastructure.persistence;

import com.example.cinema.api.domain.genre.Genre;
import com.example.cinema.api.infrastructure.persistence.projection.GenreResponseDTOProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepositoryJpa extends JpaRepository<Genre, Long> {

    Page<GenreResponseDTOProjection> findAllBy(Pageable pageable);

    Page<GenreResponseDTOProjection> findByNameContainingIgnoreCase(String name, Pageable pageable);

    boolean existsByName(String name);

    boolean existsById(Long id);

}