package com.example.cinema.api.infrastructure.repositories;

import com.example.cinema.api.domain.entities.Payment;
import com.example.cinema.api.domain.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByPurchaseId(Long purchaseId);

    @Query("""
        SELECT p.paymentStatus
        FROM Payment p
        WHERE p.id = :id
    """)
    Optional<PaymentStatus> findStatusById(@Param("id") Long id);
}
