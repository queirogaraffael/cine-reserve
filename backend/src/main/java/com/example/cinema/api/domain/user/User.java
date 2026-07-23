package com.example.cinema.api.domain.user;

import com.example.cinema.api.domain.user.exception.InvalidPasswordException;
import com.example.cinema.api.domain.user.exception.PasswordReuseException;
import com.example.cinema.api.domain.user.exception.UserUnderageException;
import com.example.cinema.api.domain.order.Order;
import com.example.cinema.api.domain.seatreservation.SeatReservation;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    private String name;

    @Column(unique = true)
    @EqualsAndHashCode.Include
    private String cpf;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    @Column(nullable = false)
    private String phone;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Embedded
    private Address address;

    @Column(name = "email_confirmed", nullable = false)
    private boolean emailConfirmed = false;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    private LocalDate dataJoined;

    private LocalDate birthdate;

    private Integer failedAttempt = 0;

    private LocalDateTime lockTime;

    private boolean isLocked = false;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Order> orders = new ArrayList<>();



    public User(String name, String email, String password, String phone, LocalDate dataJoined, UserRole role) {
        this.name = name;
        this.email = email != null ? email.trim().toLowerCase() : null;
        this.password = password;
        this.phone = phone;
        this.dataJoined = dataJoined;
        this.role = role;
        this.emailConfirmed = false;
        this.active = true;
    }

    public User(String cpf, String name, String email, String password, String phone, Gender gender, Address address, boolean emailConfirmed, LocalDate dataJoined, LocalDate birthdate, UserRole role) {
        this.cpf = cpf;
        this.name = name;
        this.email = email != null ? email.trim().toLowerCase() : null;
        this.password = password;
        this.phone = phone;
        this.gender = gender;
        this.address = address;
        this.emailConfirmed = emailConfirmed;
        this.dataJoined = dataJoined;
        validateBirthdate(birthdate);
        this.birthdate = birthdate;
        this.role = role;
        this.active = true;
    }

    public void completeProfile(Gender gender, LocalDate birthdate, String cpf) {
        if (gender != null) {
            this.gender = gender;
        }
        if (birthdate != null) {
            validateBirthdate(birthdate);
            this.birthdate = birthdate;
        }
        if (cpf != null) {
            this.cpf = cpf;
        }
    }

    private void validateBirthdate(LocalDate birthdate) {
        if (birthdate != null) {
            LocalDate dezoitoAnosAtras = LocalDate.now().minusYears(18);
            if (birthdate.isAfter(dezoitoAnosAtras)) {
                throw new UserUnderageException("O usuário deve ter pelo menos 18 anos completos.");
            }
        }
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public boolean isActive() {
        return this.active;
    }

    public void updateAddress(Address address) {
        this.address = address;
    }

    public void markEmailAsConfirmed() {
        this.emailConfirmed = true;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (role == UserRole.ADMIN)
            return List.of(
                    new SimpleGrantedAuthority("ROLE_ADMIN"),
                    new SimpleGrantedAuthority("ROLE_USER")
            );

        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public boolean isAccountNonLocked() {
        if (isLocked)
            return false;

        return lockTime == null || !lockTime.isAfter(LocalDateTime.now());
    }

    public void lockAccount(LocalDateTime until) {
        this.isLocked = true;
        this.lockTime = until;
    }

    public void unlockAccount() {
        this.isLocked = false;
        this.lockTime = null;
        this.failedAttempt = 0;
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return this.active; }

    public void changePassword(String newPassword) {
        if (newPassword == null || newPassword.isBlank()) {
            throw new InvalidPasswordException("A senha não pode ser vazia.");
        }

        if (newPassword.equals(this.password)) {
            throw new PasswordReuseException("A nova senha deve ser diferente da atual.");
        }

        this.password = newPassword;
    }
}
