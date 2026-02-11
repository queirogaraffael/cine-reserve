package com.example.cinema.api.infrastructure.security;

import com.example.cinema.api.domain.user.User;
import com.example.cinema.api.infrastructure.persistence.UserRepositoryJpa;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LoginAttemptService {

    private final UserRepositoryJpa userRepositoryJpa;

    @Value("${login.max-attempts}")
    private int MAX_ATTEMPTS;

    @Value("${login.lock-duration-minutes}")
    private long LOCK_DURATION_MINUTES;


    public LoginAttemptService(UserRepositoryJpa userRepositoryJpa) {
        this.userRepositoryJpa = userRepositoryJpa;
    }

    @Transactional
    public void loginSucceeded(String username) {
        userRepositoryJpa.resetFailedAttempts(username);
    }

    @Transactional
    public void loginFailed(String username) {
        User user = userRepositoryJpa.findByUsername(username).orElseThrow(()-> new RuntimeException("User not found"));

        if (user != null) {
            int newAttempt = user.getFailedAttempt() + 1;

            if (newAttempt >= MAX_ATTEMPTS) {
                LocalDateTime lockTime = LocalDateTime.now().plusMinutes(LOCK_DURATION_MINUTES);
                userRepositoryJpa.lockUser(username, newAttempt, lockTime);
            } else {
                userRepositoryJpa.increaseFailedAttempts(username);
            }
        }
    }
}