package com.example.cinema.api.controllers;

import com.example.cinema.api.domain.services.UserService;
import com.example.cinema.api.shared.dtos.user.UserCreatedResponseDTO;
import com.example.cinema.api.shared.dtos.user.UserRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdUser);
    }

}
