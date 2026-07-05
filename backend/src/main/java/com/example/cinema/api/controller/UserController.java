package com.example.cinema.api.controller;

import com.example.cinema.api.application.dto.user.*;
import com.example.cinema.api.application.service.UserService;
import com.example.cinema.api.infrastructure.security.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Tag(name = "Users")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Criar um novo usuário (Etapa 1)")
    @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso e tokens retornados")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "409", description = "E-mail já existe")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @PostMapping()
    public ResponseEntity<UserCreatedResponseDTO> register(@RequestBody @Valid UserRequestDTO data, HttpServletRequest request) {
        String deviceId = request.getHeader("X-Device-Id");
        String userAgent = request.getHeader("User-Agent");
        String ip = request.getRemoteAddr();

        UserCreatedResponseDTO createdUser = userService.createUser(data, deviceId, userAgent, ip);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(createdUser.getId()).toUri();

        return ResponseEntity.created(uri).body(createdUser);
    }

    @Operation(summary = "Atualizar perfil complementar do usuário (Etapa 2)")
    @ApiResponse(responseCode = "200", description = "Perfil atualizado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/me")
    public ResponseEntity<UserContextDTO> updateProfile(@AuthenticationPrincipal AuthenticatedUser principal,
                                                        @RequestBody @Valid UserProfileUpdateDTO data) {
        return ResponseEntity.ok(userService.updateProfile(principal.getId(), data));
    }

    @Operation(summary = "Atualizar endereço do usuário (Etapa 2)")
    @ApiResponse(responseCode = "200", description = "Endereço atualizado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/me/endereco")
    public ResponseEntity<UserContextDTO> updateAddress(@AuthenticationPrincipal AuthenticatedUser principal,
                                                        @RequestBody @Valid UserAddressUpdateDTO data) {
        return ResponseEntity.ok(userService.updateAddress(principal.getId(), data));
    }

    @Operation(summary = "Obter informações do usuário autenticado")
    @ApiResponse(responseCode = "200", description = "Informações do usuário obtidas com sucesso")
    @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/me")
    public ResponseEntity<UserContextDTO> getUserProfile(@AuthenticationPrincipal AuthenticatedUser principal) {
        return ResponseEntity.ok(userService.getUserProfile(principal.getId()));
    }

    @Operation(summary = "Alterar a senha do usuário autenticado")
    @ApiResponse(responseCode = "204", description = "Senha alterada com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@AuthenticationPrincipal AuthenticatedUser principal,
                                               @RequestBody @Valid ChangePasswordData data) {
        userService.changePassword(principal.getId(), data);
        return ResponseEntity.noContent().build();
    }
}
