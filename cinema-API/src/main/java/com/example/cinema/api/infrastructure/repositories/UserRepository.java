package com.example.cinema.api.infrastructure.repositories;

import com.example.cinema.api.domain.entities.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<UserDetails> findByUsername(String username);
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
