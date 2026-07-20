package com.example.cinema.api.infrastructure.persistence;

import com.example.cinema.api.domain.registration.RegistrationConfirmation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RegistrationConfirmationRepositoryJpa extends JpaRepository<RegistrationConfirmation, UUID> {

    Optional<RegistrationConfirmation> findByUserIdAndUsedFalse(UUID userId);

    @Modifying
    @Transactional
    void deleteByUserId(UUID userId);
}