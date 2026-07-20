package com.example.cinema.api.application.service;

import com.example.cinema.api.domain.registration.RegistrationConfirmation;
import com.example.cinema.api.domain.registration.event.EmailVerificationEvent;
import com.example.cinema.api.domain.registration.exception.InvalidCodeException;
import com.example.cinema.api.domain.registration.exception.ConfirmationExpiredException;
import com.example.cinema.api.domain.registration.exception.ConfirmationNotFoundException;
import com.example.cinema.api.domain.registration.exception.EmailAlreadyConfirmedException;
import com.example.cinema.api.domain.user.User;
import com.example.cinema.api.domain.user.exception.UserNotFoundException;
import com.example.cinema.api.infrastructure.persistence.RegistrationConfirmationRepositoryJpa;
import com.example.cinema.api.infrastructure.persistence.UserRepositoryJpa;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RegistrationConfirmationService {

    private final RegistrationConfirmationRepositoryJpa confirmationRepository;
    private final UserRepositoryJpa userRepository;
    private final ApplicationEventPublisher eventPublisher;
    private static final SecureRandom secureRandom = new SecureRandom();

    public RegistrationConfirmationService(RegistrationConfirmationRepositoryJpa confirmationRepository,
                                           UserRepositoryJpa userRepository,
                                           ApplicationEventPublisher eventPublisher) {
        this.confirmationRepository = confirmationRepository;
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public String generateCode(UUID userId) {
        confirmationRepository.deleteByUserId(userId);

        int number = secureRandom.nextInt(1000000);
        String code = String.format("%06d", number);

        RegistrationConfirmation confirmation = new RegistrationConfirmation(userId, code, LocalDateTime.now().plusMinutes(15));
        confirmationRepository.save(confirmation);

        return code;
    }

    @Transactional
    public void confirmEmail(UUID userId, String code) {
        RegistrationConfirmation confirmation = confirmationRepository.findByUserIdAndUsedFalse(userId)
                .orElseThrow(() -> new ConfirmationNotFoundException("Confirmação não encontrada para o usuário."));

        if (confirmation.isExpired()) {
            throw new ConfirmationExpiredException("O código de verificação expirou.");
        }

        if (!confirmation.getCode().equals(code)) {
            throw new InvalidCodeException("O código de verificação é inválido.");
        }

        confirmation.markAsUsed();
        confirmationRepository.save(confirmation);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado."));

        user.marcarEmailComoConfirmado();
        userRepository.save(user);
    }

    @Transactional
    public void resendCode(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado."));

        if (user.isEmailConfirmado()) {
            throw new EmailAlreadyConfirmedException("O e-mail deste usuário já foi confirmado.");
        }

        String code = generateCode(userId);

        eventPublisher.publishEvent(new EmailVerificationEvent(user.getId(), user.getEmail(), user.getName(), code));
    }
}