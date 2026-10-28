package com.example.cinema.api.infrastructure.persistence;

import com.example.cinema.api.domain.order.Order;
import com.example.cinema.api.domain.order.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepositoryJpa extends JpaRepository<Order, Long> {
    Optional<Order> findByIdAndUserId(Long orderId, UUID userId);
    
    boolean existsByIdAndUserId(Long orderId, UUID userId);

    @EntityGraph(attributePaths = {"movieSession.movieExhibition.movie"})
    Page<Order> findAllByUserIdAndStatusNotOrderByCreatedAtDesc(UUID userId, OrderStatus status, Pageable pageable);

    @Modifying
    @Query("""
        DELETE FROM Order o
        WHERE o.status = :status
        AND o.createdAt < :cutoff
    """)
    int deleteByStatusAndCreatedAtBefore(@Param("status") OrderStatus status,
                                         @Param("cutoff") LocalDateTime cutoff);

    @Modifying
    @Query("""
        UPDATE Order o SET o.status = 'CANCELLED'
        WHERE o.status = 'WAITING_PAYMENT'
        AND o.reservationExpiresAt < :now
    """)
    int cancelExpiredWaitingOrders(@Param("now") LocalDateTime now);
}
