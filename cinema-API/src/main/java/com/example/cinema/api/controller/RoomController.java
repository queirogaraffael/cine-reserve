package com.example.cinema.api.controller;

import com.example.cinema.api.domain.service.RoomService;
import com.example.cinema.api.shared.dtos.room.RoomRequestDTO;
import com.example.cinema.api.shared.dtos.room.RoomResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.concurrent.TimeUnit;

@Tag(name = "Rooms")
@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;
    private final CacheControl cacheControl;

    public RoomController(RoomService roomService, @Value("${cache.ttl}") long cacheTtl) {
        this.roomService = roomService;
        this.cacheControl = CacheControl.maxAge(cacheTtl, TimeUnit.SECONDS).cachePublic();
    }

    @Operation(summary = "Criar novo quarto", description = "Cria um novo quarto")
    @ApiResponse(responseCode = "201", description = "Quarto criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Erro de validação")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping()
    public ResponseEntity<RoomResponseDTO> createRoom(@RequestBody @Valid RoomRequestDTO dto) {
        RoomResponseDTO createdRoom = roomService.createRoom(dto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(createdRoom.getId()).toUri();

        return ResponseEntity.created(uri).body(createdRoom);
    }

    @Operation(summary = "Buscar quarto por ID", description = "Busca um quarto pelo ID")
    @ApiResponse(responseCode = "200", description = "Quarto encontrado")
    @ApiResponse(responseCode = "404", description = "Quarto não encontrado")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/{id}")
    public ResponseEntity<RoomResponseDTO> getRoomById(@PathVariable Long id) {
        RoomResponseDTO room = roomService.getRoomById(id);
        return ResponseEntity.ok().cacheControl(cacheControl).body(room);
    }

    @Operation(summary = "Busca paginada de todos as salas", description = "Busca todos as salas com paginação")
    @ApiResponse(responseCode = "200", description = "Lista de quartos encontrada")
    @ApiResponse(responseCode = "404", description = "Nenhum quarto encontrado")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping()
    public ResponseEntity<Page<RoomResponseDTO>> getAllRooms(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<RoomResponseDTO> rooms = roomService.getAllRooms(page, size);
        return ResponseEntity.ok().cacheControl(CacheControl.noCache().cachePrivate()).body(rooms);
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
