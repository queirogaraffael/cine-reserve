package com.example.cinema.api.domain.user;

import com.example.cinema.api.domain.user.exception.InvalidPasswordException;
import com.example.cinema.api.domain.user.exception.PasswordReuseException;
import com.example.cinema.api.domain.user.exception.UserUnderageException;
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

    private String name;

    @Column(unique = true)
    @EqualsAndHashCode.Include
    private String cpf;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    @Column(nullable = false)
    private String celular;

    @Enumerated(EnumType.STRING)
    private Sexo sexo;

    @Embedded
    private Endereco endereco;

    @Column(name = "email_confirmado", nullable = false)
    private boolean emailConfirmado = false;

    @Column(name = "ativo", nullable = false)
    private boolean ativo = true;

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

    public User(String name, String email, String password, String celular, LocalDate dataJoined, UserRole role) {
        this.name = name;
        this.email = email != null ? email.trim().toLowerCase() : null;
        this.password = password;
        this.celular = celular;
        this.dataJoined = dataJoined;
        this.role = role;
        this.emailConfirmado = false;
        this.ativo = true;
    }

    public User(String cpf, String name, String email, String password, String celular, Sexo sexo, Endereco endereco, boolean emailConfirmado, LocalDate dataJoined, LocalDate birthdate, UserRole role) {
        this.cpf = cpf;
        this.name = name;
        this.email = email != null ? email.trim().toLowerCase() : null;
        this.password = password;
        this.celular = celular;
        this.sexo = sexo;
        this.endereco = endereco;
        this.emailConfirmado = emailConfirmado;
        this.dataJoined = dataJoined;
        validateBirthdate(birthdate);
        this.birthdate = birthdate;
        this.role = role;
        this.ativo = true;
    }

    public void completarPerfil(Sexo sexo, LocalDate birthdate, String cpf) {
        if (sexo != null) {
            this.sexo = sexo;
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

    public void ativar() {
        this.ativo = true;
    }

    public void desativar() {
        this.ativo = false;
    }

    public boolean isAtivo() {
        return this.ativo;
    }

    public void atualizarEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public void marcarEmailComoConfirmado() {
        this.emailConfirmado = true;
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
    @Override public boolean isEnabled() { return this.ativo; }

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
