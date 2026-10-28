package com.example.cinema.api.shared.fixtures;

import com.example.cinema.api.infrastructure.security.AuthenticatedUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class WithMockAuthenticatedUserSecurityContextFactory implements WithSecurityContextFactory<WithMockAuthenticatedUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockAuthenticatedUser customUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        List<SimpleGrantedAuthority> authorities = Arrays.stream(customUser.roles())
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());

        Long cinemaId = customUser.cinemaId() == -1L ? null : customUser.cinemaId();
        
        AuthenticatedUser principal = new AuthenticatedUser(UUID.randomUUID(), authorities, cinemaId);

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(principal, "password", authorities);
        context.setAuthentication(auth);
        return context;
    }
}