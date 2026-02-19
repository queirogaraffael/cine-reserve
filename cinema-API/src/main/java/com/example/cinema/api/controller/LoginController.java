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

    @Operation(
            summary = "Atualizar access token",
            description = "Gera um novo token JWT de acesso utilizando um refresh token válido. " +
                    "O refresh token deve ser válido, não expirado e não revogado. " +
                    "Este endpoint permite que o usuário continue autenticado sem precisar realizar login novamente."
    )
    @ApiResponse(responseCode = "200", description = "Novo token de acesso gerado com sucesso.")
    @ApiResponse(responseCode = "400", description = "Refresh token não informado ou inválido.")
    @ApiResponse(responseCode = "401", description = "Refresh token inválido, expirado ou revogado.")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor.")
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDTO> refresh(@RequestBody @Valid RefreshTokenDTO refreshToken) {
        return ResponseEntity.ok(authService.refreshAccessToken(refreshToken));
    }

    @Operation(
            summary = "Logout do usuário",
            description = "Realiza o logout do usuário invalidando o refresh token informado. " +
                    "Após o logout, o refresh token não poderá mais ser utilizado para gerar novos tokens de acesso. " +
                    "Isso encerra efetivamente a sessão do usuário."
    )
    @ApiResponse(responseCode = "204", description = "Logout realizado com sucesso. Refresh token invalidado.")
    @ApiResponse(responseCode = "400", description = "Refresh token não informado ou inválido.")
    @ApiResponse(responseCode = "401", description = "Refresh token inválido, expirado ou já invalidado.")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor.")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody @Valid RefreshTokenDTO refreshToken) {
        authService.logout(refreshToken);
        return ResponseEntity.noContent().build();
    }

}
