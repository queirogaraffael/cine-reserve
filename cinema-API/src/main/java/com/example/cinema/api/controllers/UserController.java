package com.example.cinema.api.controllers;

import com.example.cinema.api.domain.services.UserService;
import com.example.cinema.api.shared.dtos.user.ChangePasswordData;
import com.example.cinema.api.shared.dtos.user.UserCreatedResponseDTO;
import com.example.cinema.api.shared.dtos.user.UserRequestDTO;
import com.example.cinema.api.shared.dtos.user.UserResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Tag(name = "Users")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Criar um novo usuário")
    @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "409", description = "Usuário já existe")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @PostMapping()
    public ResponseEntity<UserCreatedResponseDTO> register(@RequestBody @Valid UserRequestDTO data) {
        UserCreatedResponseDTO createdUser = userService.createUser(data);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(createdUser.getId()).toUri();

        return ResponseEntity.created(uri).body(createdUser);
    }

    @Operation(summary = "Obter informações do usuário autenticado")
    @ApiResponse(responseCode = "200", description = "Informações do usuário obtidas com sucesso")
    @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser() {
        UserResponseDTO currentUser = userService.getCurrentUser();
        return ResponseEntity.ok(currentUser);
    }


    @Operation(summary = "Alterar a senha do usuário autenticado")
    @ApiResponse(responseCode = "204", description = "Senha alterada com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@RequestBody @Valid ChangePasswordData data) {
        userService.changePassword(data);
        return ResponseEntity.noContent().build();
    }

}
