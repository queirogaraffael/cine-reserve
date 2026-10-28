package com.example.cinema.api.infrastructure.persistence;

import com.example.cinema.api.domain.movie.MovieExhibition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

@Repository
public interface MovieExhibitionRepositoryJpa extends JpaRepository<MovieExhibition, Long>, JpaSpecificationExecutor<MovieExhibition> {
    
    @Query("SELECT me FROM MovieExhibition me JOIN FETCH me.cinema WHERE me.id = :id")
    Optional<MovieExhibition> findByIdWithCinema(@Param("id") Long id);
}
