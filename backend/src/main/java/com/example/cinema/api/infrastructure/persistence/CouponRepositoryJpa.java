package com.example.cinema.api.infrastructure.persistence;

import com.example.cinema.api.domain.coupon.Coupon;
import com.example.cinema.api.domain.coupon.CouponStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CouponRepositoryJpa extends JpaRepository<Coupon, Long> {

    List<Coupon> findByUserIdAndStatusAndActiveTrue(UUID userId, CouponStatus status);

    Optional<Coupon> findByCode(String code);

    boolean existsByCode(String code);
}
