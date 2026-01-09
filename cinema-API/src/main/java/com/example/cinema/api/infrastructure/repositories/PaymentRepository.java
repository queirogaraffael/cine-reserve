package com.example.cinema.api.infrastructure.repositories;

import com.example.cinema.api.domain.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByPurchaseId(Long purchaseId);
}
