package com.example.cinema.api.application.service;

import com.example.cinema.api.domain.user.User;
import com.example.cinema.api.domain.user.UserRole;
import com.example.cinema.api.domain.user.event.UserCreatedEvent;
import com.example.cinema.api.domain.user.exception.InvalidPasswordException;
import com.example.cinema.api.domain.user.exception.UserNotFoundException;
import com.example.cinema.api.infrastructure.persistence.UserRepositoryJpa;
import com.example.cinema.api.application.dto.user.ChangePasswordData;
import com.example.cinema.api.application.dto.user.UserCreatedResponseDTO;
import com.example.cinema.api.application.dto.user.UserRequestDTO;
import com.example.cinema.api.application.dto.user.UserResponseDTO;
import com.example.cinema.api.domain.user.exception.UserAlreadyExistsException;
import com.example.cinema.api.application.mapper.UserMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService  {

    private final UserRepositoryJpa userRepositoryJpa;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final UserService self;

    public UserService(UserRepositoryJpa userRepositoryJpa, PasswordEncoder passwordEncoder, UserMapper userMapper, ApplicationEventPublisher eventPublisher, @Lazy UserService self) {
        this.userRepositoryJpa = userRepositoryJpa;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.eventPublisher = eventPublisher;
        this.self = self;
    }

    @Transactional
    public UserCreatedResponseDTO createUser(UserRequestDTO data) {

        if (userRepositoryJpa.existsByUsername(data.getUsername()) || userRepositoryJpa.existsByEmail(data.getEmail())) {
            throw new UserAlreadyExistsException("Usuário já existe");
        }

        String encryptedPassword = passwordEncoder.encode(data.getPassword());

        User newUser = new User(data.getUsername(), data.getCpf(), data.getName(),
                data.getEmail(), encryptedPassword, data.getDataJoined(),
                data.getBirthdate(), UserRole.USER);

        User user = userRepositoryJpa.save(newUser);

        eventPublisher.publishEvent(new UserCreatedEvent(user.getId(), user.getName(), user.getEmail()));

        return userMapper.toResponseDTO(user);

    }

    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepositoryJpa.existsByUsername(username);
    }

    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            throw new InsufficientAuthenticationException("Usuário não autenticado");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof Optional) {
            Optional<?> optional = (Optional<?>) principal;

            if (optional.isPresent() && optional.get() instanceof User) {
                return (User) optional.get();
            }
        }

        if (principal instanceof User) {
            return (User) principal;
        }

        throw new UserNotFoundException("Tipo de principal inesperado ou usuário não encontrado.");
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return userRepositoryJpa.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));
    }

    @Transactional(readOnly = true)
    public UserResponseDTO getCurrentUser() {
        User user = self.getAuthenticatedUser();
        return userMapper.toUserResponseDTO(user);
    }

    @Transactional
    public void changePassword(ChangePasswordData data) {

        User user= self.getAuthenticatedUser();

        if (!passwordEncoder.matches(data.getCurrentPassword(), user.getPassword())) {
            throw new InvalidPasswordException("Senha atual incorreta");
        }

        user.changePassword(passwordEncoder.encode(data.getNewPassword()));
        userRepositoryJpa.save(user);
    }

}
