package com.example.cinema.api.infrastructure.persistence;

import com.example.cinema.api.domain.payment.OrderPayment;
import com.example.cinema.api.domain.order.Order;
import com.example.cinema.api.domain.payment.PaymentStatus;
import com.example.cinema.api.application.dto.payment.response.PaymentGetResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderPaymentRepositoryJpa extends JpaRepository<OrderPayment, Long> {
    Optional<OrderPayment> findByOrderId(Long orderId);

    @Query("""
        SELECT p.paymentStatus
        FROM OrderPayment p
        WHERE p.id = :id
    """)
    Optional<PaymentStatus> findStatusById(@Param("id") Long id);

    @Query("""
        SELECT new com.example.cinema.api.application.dto.payment.response.PaymentGetResponseDTO(
            p.id,
            p.paymentDate,
            p.transactionId,
            p.paymentMethod,
            p.paymentStatus,
            p.statusDetail,
            p.order.id
        )
        FROM OrderPayment p
        WHERE p.id = :id
    """)
    Optional<PaymentGetResponseDTO> findPaymentById(@Param("id") Long id);

    boolean existsByOrder(Order order);

    @Query("""
    SELECT new com.example.cinema.api.application.dto.payment.response.PaymentGetResponseDTO(
        p.id,
        p.paymentDate,
        p.transactionId,
        p.paymentMethod,
        p.paymentStatus,
        p.statusDetail,
        p.order.id
    )
    FROM OrderPayment p
    WHERE p.order.id = :orderId
      AND p.order.user.id = :userId
    """)
    Optional<PaymentGetResponseDTO> findPaymentDtoByOrderIdAndUserId(
            @Param("orderId") Long orderId, @Param("userId") UUID userId);

}
