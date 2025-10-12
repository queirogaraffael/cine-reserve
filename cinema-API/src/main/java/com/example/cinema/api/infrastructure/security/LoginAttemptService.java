package com.example.cinema.api.infrastructure.security;

import com.example.cinema.api.domain.entities.User;
import com.example.cinema.api.infrastructure.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LoginAttemptService {

    private final UserRepository userRepository;

    @Value("${login.max-attempts}")
    private int MAX_ATTEMPTS;

    @Value("${login.lock-duration-minutes}")
    private long LOCK_DURATION_MINUTES;


    public LoginAttemptService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void loginSucceeded(String username) {
        userRepository.resetFailedAttempts(username);
    }

    @Transactional
    public void loginFailed(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(()-> new RuntimeException("User not found"));

        if (user != null) {
            int newAttempt = user.getFailedAttempt() + 1;

            if (newAttempt >= MAX_ATTEMPTS) {
                LocalDateTime lockTime = LocalDateTime.now().plusMinutes(LOCK_DURATION_MINUTES);
                userRepository.lockUser(username, newAttempt, lockTime);
                // TODO: enviar email notificando o bloqueio
            } else {
                userRepository.increaseFailedAttempts(username);
            }
        }
    }
}