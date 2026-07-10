package com.example.cinema.api.infrastructure.persistence;

import com.example.cinema.api.domain.cinema.Cinema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CinemaRepositoryJpa extends JpaRepository<Cinema, Long> {
}
