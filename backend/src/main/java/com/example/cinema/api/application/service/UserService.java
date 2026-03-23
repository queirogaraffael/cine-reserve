package com.example.cinema.api.application.service;

import com.example.cinema.api.domain.user.User;
import com.example.cinema.api.domain.user.UserRole;
import com.example.cinema.api.domain.user.event.UserCreatedEvent;
import com.example.cinema.api.domain.user.exception.InvalidPasswordException;
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

    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            throw new InsufficientAuthenticationException("Usuário não autenticado");
        }

        return (User) authentication.getPrincipal();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return userRepositoryJpa.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));
    }

    @Transactional(readOnly = true)
    public UserResponseDTO getAuthenticatedUserProfile() {
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
