package com.example.cinema.api.shared;

import com.example.cinema.api.infrastructure.persistence.UserRepositoryJpa;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class TestUtils {

    @Autowired
    private UserRepositoryJpa userRepositoryJpa;

    @Autowired
    private PasswordEncoder passwordEncoder;

}
