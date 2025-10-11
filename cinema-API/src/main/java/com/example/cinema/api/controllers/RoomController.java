package com.example.cinema.api.controllers;

import com.example.cinema.api.shared.dtos.room.RoomRequestDTO;
import com.example.cinema.api.shared.dtos.room.RoomResponseDTO;
import com.example.cinema.api.domain.services.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Rooms")
@RestController
@RequestMapping("/api/rooms")
@EnableMethodSecurity
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @Operation(summary = "Criar novo quarto", description = "Cria um novo quarto")
    @ApiResponse(responseCode = "201", description = "Quarto criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Erro de validação")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping()
    public ResponseEntity<RoomResponseDTO> createRoom(@RequestBody RoomRequestDTO dto) {
        RoomResponseDTO createdRoom = roomService.createRoom(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRoom);
    }

    @Operation(summary = "Buscar quarto por ID", description = "Busca um quarto pelo ID")
    @ApiResponse(responseCode = "200", description = "Quarto encontrado")
    @ApiResponse(responseCode = "404", description = "Quarto não encontrado")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/{id}")
    public ResponseEntity<RoomResponseDTO> getRoomById(@PathVariable Long id) {
        RoomResponseDTO room = roomService.getRoomById(id);
        return ResponseEntity.ok(room);
    }

    @Operation(summary = "Busca paginada de todos as salas", description = "Busca todos as salas com paginação")
    @ApiResponse(responseCode = "200", description = "Lista de quartos encontrada")
    @ApiResponse(responseCode = "404", description = "Nenhum quarto encontrado")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping()
    public ResponseEntity<Page<RoomResponseDTO>> getAllRooms(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<RoomResponseDTO> rooms = roomService.getAllRooms(page, size);
        return ResponseEntity.ok(rooms);
    }

    @Operation(summary = "Atualizar quarto", description = "Atualiza um quarto existente")
    @ApiResponse(responseCode = "200", description = "Quarto atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Quarto não encontrado")
    @ApiResponse(responseCode = "400", description = "Erro de validação")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/{id}")
    public ResponseEntity<RoomResponseDTO> updateRoom(
            @PathVariable Long id,
            @RequestBody RoomRequestDTO roomRequestDTO) {
        RoomResponseDTO updatedRoom = roomService.updateRoom(id, roomRequestDTO);
        return ResponseEntity.ok(updatedRoom);
    }

}
