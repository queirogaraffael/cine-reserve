package com.example.cinema.api.infrastructure.persistence;


import com.example.cinema.api.domain.entities.Purchase;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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
}
