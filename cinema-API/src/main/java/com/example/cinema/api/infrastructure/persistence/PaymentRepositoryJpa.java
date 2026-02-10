package com.example.cinema.api.infrastructure.persistence;

import com.example.cinema.api.domain.entities.Payment;
import com.example.cinema.api.domain.entities.Purchase;
import com.example.cinema.api.domain.enums.PaymentStatus;
import com.example.cinema.api.shared.dtos.payment.response.PaymentGetResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepositoryJpa extends JpaRepository<Payment, Long> {
    Optional<Payment> findByPurchaseId(Long purchaseId);

    @Query("""
        SELECT p.paymentStatus
        FROM Payment p
        WHERE p.id = :id
    """)
    Optional<PaymentStatus> findStatusById(@Param("id") Long id);

    @Query("""
        SELECT new com.example.cinema.api.shared.dtos.payment.response.PaymentGetResponseDTO(
            p.id, 
            p.paymentDate, 
            p.transactionId, 
            p.paymentMethod, 
            p.paymentStatus, 
            p.statusDetail, 
            p.purchase.id
        ) 
        FROM Payment p 
        WHERE p.id = :id
    """)
    Optional<PaymentGetResponseDTO> findPaymentById(@Param("id") Long id);

    boolean existsByPurchase(Purchase purchase);
}
