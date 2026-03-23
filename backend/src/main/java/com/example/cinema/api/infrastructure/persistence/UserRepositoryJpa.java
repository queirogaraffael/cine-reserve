package com.example.cinema.api.infrastructure.persistence;

import com.example.cinema.api.infrastructure.persistence.projection.UserNameEmailProjection;
import com.example.cinema.api.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepositoryJpa extends JpaRepository<User, UUID> {

    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Modifying
    @Query("UPDATE users u SET u.failedAttempt = 0 WHERE u.username = :username")
    void resetFailedAttempts(String username);

    @Modifying
    @Query("UPDATE users u SET u.failedAttempt = u.failedAttempt + 1 WHERE u.username = :username")
    void increaseFailedAttempts(String username);

    @Modifying
    @Query("UPDATE users u SET u.failedAttempt = :attempts, u.lockTime = :lockTime WHERE u.username = :username")
    void lockUser(String username, int attempts, LocalDateTime lockTime);

    Optional<UserNameEmailProjection> findProjectedById(UUID id);
}
