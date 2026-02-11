package com.example.cinema.api.controller;

import com.example.cinema.api.application.service.AuthService;
import com.example.cinema.api.shared.dtos.login.RefreshTokenDTO;
import com.example.cinema.api.shared.dtos.login.TokenRefreshResponseDTO;
import com.example.cinema.api.shared.dtos.login.TokenResponseDTO;
import com.example.cinema.api.shared.dtos.login.UserLoginDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth")
@RestController
@RequestMapping("/api/auth")
public class LoginController {

    private final AuthService authService;

    public LoginController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Login", description = "Realiza o login do usuário e retorna um token JWT")
    @ApiResponse(responseCode = "200", description = "Login realizado com sucesso")
    @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @PostMapping("/login")
    public ResponseEntity<TokenRefreshResponseDTO> login(@RequestBody @Valid UserLoginDTO data) {
        return ResponseEntity.ok(authService.login(data));
    }

    @Operation(summary = "Refresh Token", description = "Atualiza o token JWT utilizando um token de refresh")
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDTO> refresh(@RequestBody RefreshTokenDTO refreshToken) {
            return ResponseEntity.ok(authService.refresh(refreshToken));
    }

    @Operation(summary = "Logout", description = "Realiza o logout do usuário")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody RefreshTokenDTO refreshToken) {
        authService.logout(refreshToken);
        return ResponseEntity.noContent().build();
    }

}
