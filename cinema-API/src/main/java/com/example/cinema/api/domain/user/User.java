package com.example.cinema.api.domain.user;

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

@Entity(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String username;

    private String name;

    @Column(unique = true, nullable = false)
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

    @ToString.Exclude
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Purchase> purchases;

    @ToString.Exclude
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
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
        if(this.role == UserRole.ADMIN)
            return List.of(
                    new SimpleGrantedAuthority("ROLE_ADMIN"),
                    new SimpleGrantedAuthority("ROLE_USER"));
        else
            return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        if (this.isLocked) {
            return false;
        }

        if (this.lockTime != null && this.lockTime.isAfter(LocalDateTime.now())) {
            return false;
        }

        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
