package com.example.cinema.api.domain.repositories;

import com.example.cinema.api.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    UserDetails findByUsername(String username);

    boolean existsByUsername(String username);
}
