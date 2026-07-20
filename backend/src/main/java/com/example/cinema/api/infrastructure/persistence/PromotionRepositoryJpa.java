package com.example.cinema.api.infrastructure.persistence;

import com.example.cinema.api.domain.promotion.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PromotionRepositoryJpa extends JpaRepository<Promotion, Long> {
    List<Promotion> findByCinemaIdAndActiveTrue(Long cinemaId);
}
