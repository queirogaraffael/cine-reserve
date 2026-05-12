package com.example.cinema.api.domain.user;

import com.example.cinema.api.domain.user.exception.InvalidPasswordException;
import com.example.cinema.api.domain.user.exception.PasswordReuseException;
import com.example.cinema.api.domain.purchase.Purchase;
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

    @Column(unique = true, nullable = false)
    private String username;

    private String name;

    @Column(unique = true, nullable = false)
    @EqualsAndHashCode.Include
    private String cpf;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    private LocalDate dataJoined;

    private LocalDate birthdate;

    private Integer failedAttempt = 0;

    private LocalDateTime lockTime;

    private boolean isLocked = false;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Purchase> purchases = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<SeatReservation> seatReservations = new ArrayList<>();

    public User(String username, String cpf, String name, String email, String password, LocalDate dataJoined, LocalDate birthdate, UserRole role) {
        this.username = username;
        this.cpf = cpf;
        this.name = name;
        this.email = email;
        this.password = password;
        this.dataJoined = dataJoined;
        this.birthdate = birthdate;
        this.role = role;
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
    @Override public boolean isEnabled() { return true; }

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
