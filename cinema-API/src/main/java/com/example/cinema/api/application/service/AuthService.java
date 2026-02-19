package com.example.cinema.api.application.service;

import com.example.cinema.api.domain.user.User;
import com.example.cinema.api.infrastructure.security.service.RefreshTokenService;
import com.example.cinema.api.shared.dtos.login.RefreshTokenDTO;
import com.example.cinema.api.shared.dtos.login.TokenRefreshResponseDTO;
import com.example.cinema.api.infrastructure.security.service.TokenService;
import com.example.cinema.api.shared.dtos.login.TokenResponseDTO;
import com.example.cinema.api.shared.dtos.login.UserLoginDTO;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final RefreshTokenService refreshTokenService;

    public AuthService(AuthenticationManager authenticationManager, TokenService tokenService, RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.refreshTokenService = refreshTokenService;
    }

    public TokenRefreshResponseDTO login(UserLoginDTO data) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(data.getUsername(), data.getPassword())
        );
        var user = (User) auth.getPrincipal();

        String jwt = tokenService.generateToken(user);
        String refreshToken = refreshTokenService.generateRefreshToken(data.getUsername());

        return new TokenRefreshResponseDTO(jwt,refreshToken);
    }

    public TokenResponseDTO refreshAccessToken(RefreshTokenDTO refreshToken) {

        if (!refreshTokenService.validateRefreshToken(refreshToken.getToken())) {
            throw new RuntimeException("Refresh Token inválido");
        }

        String username = refreshTokenService.getUsernameFromRefreshToken(refreshToken.getToken());
        String token = tokenService.generateJwt(username);

        return new TokenResponseDTO(token);
    }

    public void logout(RefreshTokenDTO refreshToken) {
        refreshTokenService.invalidateRefreshToken(refreshToken.getToken());
    }
}
