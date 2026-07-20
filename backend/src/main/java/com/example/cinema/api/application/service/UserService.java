package com.example.cinema.api.application.service;

import com.example.cinema.api.application.dto.user.*;
import com.example.cinema.api.application.mapper.UserMapper;
import com.example.cinema.api.domain.user.Endereco;
import com.example.cinema.api.domain.user.User;
import com.example.cinema.api.domain.user.UserRole;
import com.example.cinema.api.domain.user.event.UserCreatedEvent;
import com.example.cinema.api.domain.user.exception.InvalidPasswordException;
import com.example.cinema.api.domain.user.exception.UserAlreadyExistsException;
import com.example.cinema.api.domain.user.exception.UserNotFoundException;
import com.example.cinema.api.infrastructure.persistence.UserRepositoryJpa;
import com.example.cinema.api.infrastructure.security.service.TokenService;
import com.example.cinema.api.infrastructure.security.service.UserSessionService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    private final UserRepositoryJpa userRepositoryJpa;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final RegistrationConfirmationService registrationConfirmationService;
    private final TokenService tokenService;
    private final UserSessionService userSessionService;

    public UserService(UserRepositoryJpa userRepositoryJpa,
                       PasswordEncoder passwordEncoder,
                       UserMapper userMapper,
                       ApplicationEventPublisher eventPublisher,
                       RegistrationConfirmationService registrationConfirmationService,
                       TokenService tokenService,
                       UserSessionService userSessionService) {
        this.userRepositoryJpa = userRepositoryJpa;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.eventPublisher = eventPublisher;
        this.registrationConfirmationService = registrationConfirmationService;
        this.tokenService = tokenService;
        this.userSessionService = userSessionService;
    }

    @Transactional
    public UserCreatedResponseDTO createUser(UserRequestDTO data, String deviceId, String userAgent, String ip) {
        String normalizedEmail = data.getEmail() != null ? data.getEmail().trim().toLowerCase() : null;
        if (userRepositoryJpa.existsByEmail(normalizedEmail)) {
            throw new UserAlreadyExistsException("E-mail já está em uso");
        }

        String encryptedPassword = passwordEncoder.encode(data.getPassword());

        User newUser = new User(
                data.getName(),
                normalizedEmail,
                encryptedPassword,
                data.getCelular(),
                LocalDate.now(),
                UserRole.USER
        );

        User user = userRepositoryJpa.save(newUser);

        String verificationCode = registrationConfirmationService.generateCode(user.getId());

        eventPublisher.publishEvent(new UserCreatedEvent(user.getId(), user.getName(), user.getEmail(), verificationCode));

        String jwt = tokenService.generateToken(user);
        String refreshToken = userSessionService.createUserSession(user.getId(), deviceId, userAgent, ip);

        return new UserCreatedResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                jwt,
                refreshToken
        );
    }

    @Transactional
    public UserContextDTO updateProfile(UUID userId, UserProfileUpdateDTO data) {
        User user = findById(userId);
        user.completarPerfil(data.getSexo(), data.getBirthdate(), data.getCpf());
        userRepositoryJpa.save(user);
        return getUserProfile(userId);
    }

    @Transactional
    public UserContextDTO updateAddress(UUID userId, UserAddressUpdateDTO data) {
        User user = findById(userId);
        Endereco endereco = new Endereco(
                data.getCep(),
                data.getLogradouro(),
                data.getNumero(),
                data.getComplemento(),
                data.getBairro(),
                data.getCidade(),
                data.getEstado()
        );
        user.atualizarEndereco(endereco);
        userRepositoryJpa.save(user);
        return getUserProfile(userId);
    }

    @Transactional(readOnly = true)
    public User findById(UUID id) {
        return userRepositoryJpa.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado: " + id));
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        String normalizedEmail = email != null ? email.trim().toLowerCase() : null;
        return userRepositoryJpa.findByEmail(normalizedEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));
    }

    @Transactional(readOnly = true)
    public UserContextDTO getUserProfile(UUID userId) {
        User user = findById(userId);
        return new UserContextDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCelular(),
                user.getSexo(),
                user.isEmailConfirmado(),
                user.isAtivo(),
                user.getEndereco(),
                user.getRole()
        );
    }

    @Transactional
    public void changePassword(UUID userId, ChangePasswordData data) {
        User user = findById(userId);

        if (!passwordEncoder.matches(data.getCurrentPassword(), user.getPassword())) {
            throw new InvalidPasswordException("Senha atual incorreta");
        }

        user.changePassword(passwordEncoder.encode(data.getNewPassword()));
        userRepositoryJpa.save(user);
    }
}
