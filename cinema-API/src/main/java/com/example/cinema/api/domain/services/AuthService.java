package com.example.cinema.api.domain.services;

import com.example.cinema.api.domain.entities.User;
import com.example.cinema.api.shared.dtos.login.RefreshTokenDTO;
import com.example.cinema.api.shared.dtos.login.TokenRefreshResponseDTO;
import com.example.cinema.api.infrastructure.security.TokenService;
import com.example.cinema.api.shared.dtos.login.TokenResponseDTO;
import com.example.cinema.api.shared.dtos.login.UserLoginDTO;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AuthService(AuthenticationManager authenticationManager, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    public TokenRefreshResponseDTO login(UserLoginDTO data) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(data.getUsername(), data.getPassword())
        );
        var user = (User) auth.getPrincipal();

        String jwt = tokenService.generateToken(user);
        String refreshToken = tokenService.generateRefreshToken(data.getUsername());

        return new TokenRefreshResponseDTO(jwt,refreshToken);
    }

    public TokenResponseDTO refresh(RefreshTokenDTO refreshToken) {

        if (!tokenService.validateRefreshToken(refreshToken.getToken())) {
            throw new RuntimeException("Refresh Token inválido");
        }

        String username = tokenService.getUsernameFromRefreshToken(refreshToken.getToken());
        String token = tokenService.generateJwt(username);

        return new TokenResponseDTO(token);
    }

    public void logout(RefreshTokenDTO refreshToken) {
        tokenService.invalidateRefreshToken(refreshToken.getToken());
    }
}
