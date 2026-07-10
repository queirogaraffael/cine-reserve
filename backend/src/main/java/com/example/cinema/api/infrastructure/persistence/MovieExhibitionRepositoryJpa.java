package com.example.cinema.api.infrastructure.persistence;

import com.example.cinema.api.domain.movie.MovieExhibition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieExhibitionRepositoryJpa extends JpaRepository<MovieExhibition, Long>, JpaSpecificationExecutor<MovieExhibition> {
}
