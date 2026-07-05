package com.example.cinema.api.controller;

import com.example.cinema.api.application.dto.user.ConfirmarEmailDTO;
import com.example.cinema.api.application.service.ConfirmacaoCadastroService;
import com.example.cinema.api.infrastructure.security.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Verification")
@RestController
@RequestMapping("/api/users/me/confirmar-email")
public class ConfirmacaoController {

    private final ConfirmacaoCadastroService confirmacaoService;

    public ConfirmacaoController(ConfirmacaoCadastroService confirmacaoService) {
        this.confirmacaoService = confirmacaoService;
    }

    @Operation(summary = "Confirmar e-mail com código de 6 dígitos")
    @ApiResponse(responseCode = "200", description = "E-mail confirmado com sucesso")
    @ApiResponse(responseCode = "400", description = "Código inválido ou expirado")
    @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    @ApiResponse(responseCode = "404", description = "Confirmação não encontrada")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping()
    public ResponseEntity<Void> confirmarEmail(@AuthenticationPrincipal AuthenticatedUser principal,
                                               @RequestBody @Valid ConfirmarEmailDTO data) {
        confirmacaoService.confirmarEmail(principal.getId(), data.getCodigo());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Reenviar código de verificação para o e-mail")
    @ApiResponse(responseCode = "200", description = "Código reenviado com sucesso")
    @ApiResponse(responseCode = "400", description = "E-mail já confirmado")
    @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/reenviar")
    public ResponseEntity<Void> reenviarCodigo(@AuthenticationPrincipal AuthenticatedUser principal) {
        confirmacaoService.reenviarCodigo(principal.getId());
        return ResponseEntity.ok().build();
    }
}
