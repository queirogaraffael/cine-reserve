package com.example.cinema.api.infrastructure.security.service;

import com.example.cinema.api.domain.user.User;
import com.example.cinema.api.infrastructure.persistence.UserRepositoryJpa;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LoginAttemptService {

    private final UserRepositoryJpa userRepositoryJpa;
    private final UserSessionService userSessionService;

    @Value("${login.max-attempts}")
    private int MAX_ATTEMPTS;

    @Value("${login.lock-duration-minutes}")
    private long LOCK_DURATION_MINUTES;

    public LoginAttemptService(UserRepositoryJpa userRepositoryJpa, UserSessionService userSessionService) {
        this.userRepositoryJpa = userRepositoryJpa;
        this.userSessionService = userSessionService;
    }

    @Transactional
    public void loginSucceeded(String email) {
        if (email != null) {
            userRepositoryJpa.resetFailedAttempts(email.trim().toLowerCase());
        }
    }

    @Transactional
    public void loginFailed(String email) {
        if (email == null) return;
        String normalizedEmail = email.trim().toLowerCase();
        var userOptional = userRepositoryJpa.findByEmail(normalizedEmail);
        if (userOptional.isEmpty()) {
            return;
        }
        User user = userOptional.get();

        int newAttempt = user.getFailedAttempt() + 1;

        if (newAttempt >= MAX_ATTEMPTS) {
            LocalDateTime lockTime = LocalDateTime.now().plusMinutes(LOCK_DURATION_MINUTES);
            userRepositoryJpa.lockUser(normalizedEmail, newAttempt, lockTime);
            userSessionService.invalidateAllUserSessions(user.getId());
        } else {
            userRepositoryJpa.increaseFailedAttempts(normalizedEmail);
        }
    }
}