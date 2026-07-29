package com.example.cinema.api.infrastructure.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.UUID;

public class AuthenticatedUser implements UserDetails {

    private final UUID id;
    private final Collection<? extends GrantedAuthority> authorities;
    private final Long cinemaId;

    public AuthenticatedUser(UUID id, Collection<? extends GrantedAuthority> authorities, Long cinemaId) {
        this.id = id;
        this.authorities = authorities;
        this.cinemaId = cinemaId;
    }

    public UUID getId() {
        return id;
    }

    public Long getCinemaId() {
        return cinemaId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return id.toString();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
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
