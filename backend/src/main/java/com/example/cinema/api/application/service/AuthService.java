package com.example.cinema.api.application.service;

import com.example.cinema.api.application.dto.login.UserSessionDTO;
import com.example.cinema.api.domain.user.User;
import com.example.cinema.api.infrastructure.security.service.UserSessionService;
import com.example.cinema.api.application.dto.login.RefreshTokenDTO;
import com.example.cinema.api.application.dto.login.TokenRefreshResponseDTO;
import com.example.cinema.api.infrastructure.security.service.TokenService;
import com.example.cinema.api.application.dto.login.TokenResponseDTO;
import com.example.cinema.api.application.dto.login.UserLoginDTO;
import com.example.cinema.api.domain.exception.RefreshTokenInvalidException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserService userService;
    private final UserSessionService userSessionService;

    public AuthService(AuthenticationManager authenticationManager, TokenService tokenService, UserService userService, UserSessionService userSessionService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.userService = userService;
        this.userSessionService = userSessionService;
    }

    public TokenRefreshResponseDTO login(UserLoginDTO data, String deviceId, String userAgent, String ip) {

        var auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(data.getUsername(), data.getPassword()));

        var user = (User) auth.getPrincipal();

        String jwt = tokenService.generateToken(user);

        String refreshToken = userSessionService.createUserSession(user.getId(), deviceId, userAgent, ip);

        return new TokenRefreshResponseDTO(jwt,refreshToken);
    }

    public TokenResponseDTO generateAccessTokenFromValidRefreshToken(RefreshTokenDTO refreshToken) {

        if (!userSessionService.validateSession(refreshToken.getToken())) {
            throw new RuntimeException("Refresh Token inválido");
        }

        UUID userId = userSessionService.getUserIdFromSession(refreshToken.getToken());
        String token = tokenService.generateJwt(userId);

        return new TokenResponseDTO(token);
    }

    public TokenRefreshResponseDTO rotateRefreshTokenAndIssueAccessToken(String oldRefreshToken) {

        UserSessionDTO session = userSessionService.getSession(oldRefreshToken);

        if (session == null) {
            throw new RefreshTokenInvalidException("Refresh token inválido, expirado ou reutilizado");
        }

        UUID userId = session.getUserId();

        userSessionService.invalidateSession(oldRefreshToken);

        String newRefreshToken = userSessionService.createUserSession(userId, session.getDeviceId(), session.getUserAgent(), session.getIp());

        String newAccessToken = tokenService.generateJwt(userId.toString());

        return new TokenRefreshResponseDTO(newAccessToken, newRefreshToken);
    }

    public Set<UserSessionDTO> getValidSessions(){

        User user = userService.getAuthenticatedUser();

        UUID userId = user.getId();

        Set<String> validSessions = userSessionService.getValidSessions(userId);

        Set<UserSessionDTO> sessions = new HashSet<>();

        for (String hashedToken : validSessions) {

            UserSessionDTO session = userSessionService.getSession(hashedToken);

            if (session != null) {
                sessions.add(session);
            }
        }
        return sessions;
    }

    public void logoutAllUserSessions(){
        User user = userService.getAuthenticatedUser();

        UUID userId = user.getId();

        userSessionService.invalidateAllUserSessions(userId);
    }

    public void logout(RefreshTokenDTO refreshToken) {
        userSessionService.invalidateSession(refreshToken.getToken());
    }

}
