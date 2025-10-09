package com.example.cinema.api.domain.services;

import com.example.cinema.api.domain.entities.User;
import com.example.cinema.api.domain.enums.UserRole;
import com.example.cinema.api.domain.user.event.UserCreatedEvent;
import com.example.cinema.api.infrastructure.repositories.UserRepository;
import com.example.cinema.api.shared.dtos.user.UserCreatedResponseDTO;
import com.example.cinema.api.shared.dtos.user.UserRequestDTO;
import com.example.cinema.api.shared.exceptions.UserAlreadyExistsException;
import com.example.cinema.api.shared.exceptions.UserNotAuthenticatedException;
import com.example.cinema.api.shared.mappers.UserMapper;
import org.springframework.context.ApplicationEventPublisher;
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

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final ApplicationEventPublisher eventPublisher;

    public UserService(UserRepository userRepository,
                      PasswordEncoder passwordEncoder,
                      UserMapper userMapper,
                      ApplicationEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.eventPublisher = eventPublisher;
    }


    @Transactional
    public UserCreatedResponseDTO createUser(UserRequestDTO data) {

        System.out.println("Creating user with username: " + data.getUsername() + " and email: " + data.getEmail());

        if (userRepository.existsByUsername(data.getUsername()) || userRepository.existsByEmail(data.getEmail())) {
            throw new UserAlreadyExistsException("Usuário já existe");
        }

        String encryptedPassword = passwordEncoder.encode(data.getPassword());

        User newUser = new User(data.getUsername(), data.getName(),
                data.getEmail(), encryptedPassword, data.getDataJoined(),
                data.getBirthdate(), UserRole.USER, data.getCategory());

        User user = userRepository.save(newUser);

        eventPublisher.publishEvent(new UserCreatedEvent(this, data.getEmail(), data.getName()));

        return userMapper.toResponseDTO(user);

    }

    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Transactional(readOnly = true)
    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new UserNotAuthenticatedException("Usuário não autenticado");
        }

        return (User) authentication.getPrincipal();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));
    }
}
