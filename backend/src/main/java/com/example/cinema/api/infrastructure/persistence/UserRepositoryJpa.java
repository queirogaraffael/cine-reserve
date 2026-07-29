package com.example.cinema.api.infrastructure.persistence;

import com.example.cinema.api.infrastructure.persistence.projection.UserNameEmailProjection;
import com.example.cinema.api.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;

@Repository
public interface UserRepositoryJpa extends JpaRepository<User, UUID> {

    @EntityGraph(attributePaths = { "cinema" })
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Page<User> findByCinemaIdAndRole(Long cinemaId, com.example.cinema.api.domain.user.UserRole role,
            org.springframework.data.domain.Pageable pageable);

    @Modifying
    @Query("UPDATE users u SET u.failedAttempt = 0 WHERE u.email = :email")
    void resetFailedAttempts(String email);

    @Modifying
    @Query("UPDATE users u SET u.failedAttempt = u.failedAttempt + 1 WHERE u.email = :email")
    void increaseFailedAttempts(String email);

    @Modifying
    @Query("UPDATE users u SET u.failedAttempt = :attempts, u.lockTime = :lockTime WHERE u.email = :email")
    void lockUser(String email, int attempts, LocalDateTime lockTime);

    Optional<UserNameEmailProjection> findProjectedById(UUID id);
}
