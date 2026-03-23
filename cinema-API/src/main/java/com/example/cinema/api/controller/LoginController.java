package com.example.cinema.api.controller;

import com.example.cinema.api.application.dto.login.*;
import com.example.cinema.api.application.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Tag(name = "Auth")
@RestController
@RequestMapping("/api/auth")
public class LoginController {

    private final AuthService authService;

    public LoginController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(
            summary = "Login do usuário",
            description = """
                Autentica o usuário utilizando username e senha e retorna um Access Token (JWT)
                e um Refresh Token.

                Este endpoint também registra a sessão do dispositivo para controle de segurança.

                Informações coletadas:

                • X-Device-Id → Identificador único do dispositivo (obrigatório)
                • User-Agent → Informações do navegador ou aplicativo cliente
                • Endereço IP → Obtido automaticamente da requisição

                Esses dados são utilizados para:

                • Controle de sessões por dispositivo
                • Revogação individual de sessões
                • Maior segurança contra uso indevido de tokens
                """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso. Tokens retornados.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenRefreshResponseDTO.class))),

            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content)})
    @Parameter(
            name = "X-Device-Id",
            description = "Identificador único do dispositivo. Recomenda-se utilizar UUID.",
            required = true,
            in = ParameterIn.HEADER,
            example = "550e8400-e29b-41d4-a716-446655440000"
    )
    @PostMapping("/login")
    public ResponseEntity<TokenRefreshResponseDTO> login(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Credenciais do usuário", required = true, content = @Content(schema = @Schema(implementation = UserLoginDTO.class)))
            @RequestBody @Valid UserLoginDTO data, HttpServletRequest request) {

        String deviceId = request.getHeader("X-Device-Id");
        String userAgent = request.getHeader("User-Agent");
        String ip = request.getRemoteAddr();

        return ResponseEntity.ok(authService.login(data, deviceId, userAgent, ip));
    }

    @Operation(summary = "Atualizar access token",
            description = "Gera um novo token JWT de acesso utilizando um refresh token válido. " +
                    "O refresh token deve ser válido, não expirado e não revogado. " +
                    "Este endpoint permite que o usuário continue autenticado sem precisar realizar login novamente.")
    @ApiResponse(responseCode = "200", description = "Novo token de acesso gerado com sucesso.")
    @ApiResponse(responseCode = "400", description = "Refresh token não informado ou inválido.")
    @ApiResponse(responseCode = "401", description = "Refresh token inválido, expirado ou revogado.")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor.")
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDTO> refresh(@RequestBody @Valid RefreshTokenDTO refreshToken) {
        return ResponseEntity.ok(authService.generateAccessTokenFromValidRefreshToken(refreshToken));
    }

    @Operation(
            summary = "Rotacionar refresh token",
            description = """
            Invalida o refresh token atual e gera um novo refresh token junto com um novo access token.

            Este processo aumenta a segurança e previne reutilização de refresh tokens roubados.

            Comportamento:

            • O refresh token antigo é invalidado imediatamente
            • Um novo refresh token é criado
            • Um novo access token é emitido
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tokens rotacionados com sucesso.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TokenRefreshResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Refresh token não informado", content = @Content),
            @ApiResponse(responseCode = "401", description = "Refresh token inválido, expirado ou reutilizado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content)
    })
    @PostMapping("/refresh/rotate")
    public ResponseEntity<TokenRefreshResponseDTO> rotateRefreshToken(@RequestBody @Valid RefreshTokenDTO refreshToken) {
        return ResponseEntity.ok(authService.rotateRefreshTokenAndIssueAccessToken(refreshToken.getToken()));
    }

    @Operation(
            summary = "Listar sessões ativas",
            description = """
            Retorna todas as sessões ativas do usuário autenticado.

            Cada sessão representa um dispositivo onde o usuário realizou login.

            Informações retornadas:

            • Device ID
            • User Agent
            • Endereço IP
            • Data de criação

            Permite ao usuário visualizar e gerenciar seus dispositivos conectados.
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sessões retornadas com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserSessionDTO.class))),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content)})
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/sessions")
    public ResponseEntity<Set<UserSessionDTO>> getSessions(){
        return ResponseEntity.ok(authService.getValidSessions());
    }

    @Operation(
            summary = "Logout de todas as sessões",
            description = """
            Encerra todas as sessões ativas do usuário autenticado.

            Após esta operação:

            • Todos os refresh tokens serão invalidados
            • Todos os dispositivos serão desconectados
            • Será necessário realizar login novamente

            Recomendado em caso de:

            • Dispositivo roubado
            • Suspeita de comprometimento
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Todas as sessões encerradas com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content)})
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/logout/all")
    public ResponseEntity<Void> logoutAll(){
        authService.logoutAllUserSessions();

        return ResponseEntity.noContent().build();
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
