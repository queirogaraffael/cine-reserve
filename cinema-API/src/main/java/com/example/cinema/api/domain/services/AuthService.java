package com.example.cinema.api.domain.services;

import com.example.cinema.api.domain.entities.User;
import com.example.cinema.api.infrastructure.repositories.UserRepository;
import com.example.cinema.api.infrastructure.security.TokenService;
import com.example.cinema.api.shared.dtos.login.TokenResponseDTO;
import com.example.cinema.api.shared.dtos.login.UserLoginDTO;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AuthService(AuthenticationManager authenticationManager, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    public TokenResponseDTO login(UserLoginDTO data) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(data.getUsername(), data.getPassword())
        );
        var user = (User) auth.getPrincipal();
        return new TokenResponseDTO(tokenService.generateToken(user));
    }

}
