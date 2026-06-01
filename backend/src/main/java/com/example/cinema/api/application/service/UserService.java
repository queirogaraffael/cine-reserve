package com.example.cinema.api.application.service;

import com.example.cinema.api.application.dto.user.UserContextDTO;
import com.example.cinema.api.domain.user.User;
import com.example.cinema.api.domain.user.UserRole;
import com.example.cinema.api.domain.user.event.UserCreatedEvent;
import com.example.cinema.api.domain.user.exception.InvalidPasswordException;
import com.example.cinema.api.domain.user.exception.UserNotFoundException;
import com.example.cinema.api.infrastructure.persistence.UserRepositoryJpa;
import com.example.cinema.api.application.dto.user.ChangePasswordData;
import com.example.cinema.api.application.dto.user.UserCreatedResponseDTO;
import com.example.cinema.api.application.dto.user.UserRequestDTO;
import com.example.cinema.api.domain.user.exception.UserAlreadyExistsException;
import com.example.cinema.api.application.mapper.UserMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    private final UserRepositoryJpa userRepositoryJpa;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final ApplicationEventPublisher eventPublisher;

    public UserService(UserRepositoryJpa userRepositoryJpa, PasswordEncoder passwordEncoder, UserMapper userMapper, ApplicationEventPublisher eventPublisher) {
        this.userRepositoryJpa = userRepositoryJpa;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public UserCreatedResponseDTO createUser(UserRequestDTO data) {

        if (userRepositoryJpa.existsByUsername(data.getUsername()))
            throw new UserAlreadyExistsException("Username já está em uso");

        if (userRepositoryJpa.existsByEmail(data.getEmail()))
            throw new UserAlreadyExistsException("E-mail já está em uso");

        String encryptedPassword = passwordEncoder.encode(data.getPassword());

        User newUser = new User(data.getUsername(), data.getCpf(), data.getName(),
                data.getEmail(), encryptedPassword, data.getDataJoined(),
                data.getBirthdate(), UserRole.USER);

        User user = userRepositoryJpa.save(newUser);

        eventPublisher.publishEvent(new UserCreatedEvent(user.getId(), user.getName(), user.getEmail()));

        return userMapper.toResponseDTO(user);
    }

    @Transactional(readOnly = true)
    public User findById(UUID id) {
        return userRepositoryJpa.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado: " + id));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepositoryJpa.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));
    }

    @Transactional(readOnly = true)
    public UserContextDTO getUserProfile(UUID userId) {
        User user = findById(userId);
        return new UserContextDTO(
                user.getId(),
                user.getName(),
                user.getUsername(),
                user.getEmail(),
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
