package com.example.cinema.api.application.service;

import com.example.cinema.api.application.dto.user.UserCreatedResponseDTO;
import com.example.cinema.api.application.dto.user.UserRequestDTO;
import com.example.cinema.api.application.dto.user.CinemaAdminResponseDTO;
import com.example.cinema.api.domain.cinema.Cinema;
import com.example.cinema.api.domain.cinema.exception.CinemaNotFoundException;
import com.example.cinema.api.domain.cinema.exception.UserDoesNotBelongToCinemaException;
import com.example.cinema.api.domain.cinema.exception.UserIsNotCinemaAdminException;
import com.example.cinema.api.domain.user.User;
import com.example.cinema.api.domain.user.UserRole;
import com.example.cinema.api.domain.user.exception.UserAlreadyExistsException;
import com.example.cinema.api.domain.user.exception.UserNotFoundException;
import com.example.cinema.api.infrastructure.persistence.CinemaRepositoryJpa;
import com.example.cinema.api.infrastructure.persistence.UserRepositoryJpa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;
import org.springframework.security.access.AccessDeniedException;
import com.example.cinema.api.infrastructure.security.AuthenticatedUser;

@Service
public class CinemaAdminService {

    private final UserRepositoryJpa userRepositoryJpa;
    private final CinemaRepositoryJpa cinemaRepositoryJpa;
    private final PasswordEncoder passwordEncoder;

    public CinemaAdminService(UserRepositoryJpa userRepositoryJpa, CinemaRepositoryJpa cinemaRepositoryJpa, PasswordEncoder passwordEncoder) {
        this.userRepositoryJpa = userRepositoryJpa;
        this.cinemaRepositoryJpa = cinemaRepositoryJpa;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserCreatedResponseDTO createCinemaAdmin(Long cinemaId, UserRequestDTO request) {
        Cinema cinema = cinemaRepositoryJpa.findById(cinemaId)
                .orElseThrow(() -> new CinemaNotFoundException("Cinema não encontrado"));

        if (userRepositoryJpa.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("E-mail já está em uso.");
        }

        User admin = new User(
                request.getName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getPhone(),
                LocalDate.now(),
                UserRole.CINEMA_ADMIN
        );
        admin.setCinema(cinema);
        admin.markEmailAsConfirmed();
        
        userRepositoryJpa.save(admin);

        return new UserCreatedResponseDTO(admin.getId(), admin.getName(), admin.getEmail(), admin.getRole(), null, null);
    }

    public Page<CinemaAdminResponseDTO> listCinemaAdmins(Long cinemaId, Pageable pageable) {
        if (!cinemaRepositoryJpa.existsById(cinemaId)) {
            throw new CinemaNotFoundException("Cinema não encontrado");
        }
        return userRepositoryJpa.findByCinemaIdAndRole(cinemaId, UserRole.CINEMA_ADMIN, pageable)
                .map(user -> new CinemaAdminResponseDTO(user.getId(), user.getName(), user.getEmail(), user.getPhone()));
    }

    @Transactional
    public void removeCinemaAdmin(Long cinemaId, UUID userId) {
        User user = userRepositoryJpa.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        if (user.getCinema() == null || !user.getCinema().getId().equals(cinemaId)) {
            throw new UserDoesNotBelongToCinemaException("O usuário não pertence a este cinema.");
        }
        
        if (user.getRole() != UserRole.CINEMA_ADMIN) {
            throw new UserIsNotCinemaAdminException("O usuário não é um CINEMA_ADMIN.");
        }

        user.deactivate();
        user.removeAdminPrivileges();
        userRepositoryJpa.save(user);
    }

    public void validateCinemaOwnership(AuthenticatedUser user, Long targetCinemaId) {
        boolean isCinemaAdmin = user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CINEMA_ADMIN"));
        
        if (isCinemaAdmin && (user.getCinemaId() == null || !user.getCinemaId().equals(targetCinemaId))) {
            throw new AccessDeniedException("Você não tem permissão para acessar ou modificar este cinema.");
        }
    }
}
