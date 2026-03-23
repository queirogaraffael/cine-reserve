package com.example.cinema.api.infrastructure.persistence;


import com.example.cinema.api.domain.purchase.Purchase;
import com.example.cinema.api.domain.user.User;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PurchaseRepositoryJpa extends JpaRepository<Purchase, Long> {


    @Modifying
    @Query("""
        UPDATE Purchase p
        SET p.idempotencyKey = :idempotencyKey
        WHERE p.id = :id
    """)
    int updateIdempotencyKey(@Param("id") Long id,
                             @Param("idempotencyKey") String idempotencyKey);

    Optional<Purchase> findByIdempotencyKeyAndUser(String idempotencyKey, User user);

    Optional<Purchase> findByIdAndUser(Long purchaseId, User user);
}
