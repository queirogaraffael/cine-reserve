package com.example.cinema.api.controller;

import com.example.cinema.api.application.service.RoomService;
import com.example.cinema.api.application.dto.room.RoomRequestDTO;
import com.example.cinema.api.application.dto.room.RoomResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.example.cinema.api.infrastructure.security.AuthenticatedUser;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Tag(name = "Rooms")
@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;
    private final CacheControl shortCachePrivate;
    private final CacheControl noCachePrivate;

    public RoomController(
            RoomService roomService,
            @Qualifier("shortCachePrivate") CacheControl shortCachePrivate,
            @Qualifier("noCachePrivate") CacheControl noCachePrivate) {
        this.roomService = roomService;
        this.shortCachePrivate = shortCachePrivate;
        this.noCachePrivate = noCachePrivate;
    }

    @Operation(summary = "Criar novo quarto", description = "Cria uma nova sala")
    @ApiResponse(responseCode = "201", description = "Sala criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Erro de validação")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'CINEMA_ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping()
    public ResponseEntity<RoomResponseDTO> createRoom(@RequestBody @Valid RoomRequestDTO dto, @AuthenticationPrincipal AuthenticatedUser user) {
        RoomResponseDTO createdRoom = roomService.createRoom(dto, user);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(createdRoom.getId()).toUri();

        return ResponseEntity.created(uri).body(createdRoom);
    }

    @Operation(summary = "Buscar quarto por ID", description = "Busca uma sala pelo ID")
    @ApiResponse(responseCode = "200", description = "Sala encontrada")
    @ApiResponse(responseCode = "404", description = "Sala não encontrada")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'CINEMA_ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/{id}")
    public ResponseEntity<RoomResponseDTO> getRoomById(@PathVariable Long id) {
        return ResponseEntity.ok()
                .cacheControl(shortCachePrivate)
                .body(roomService.getRoomById(id));
    }

    @Operation(summary = "Busca paginada de todos as salas", description = "Busca todos as salas com paginação")
    @ApiResponse(responseCode = "200", description = "Lista de salas encontrada")
    @ApiResponse(responseCode = "404", description = "Nenhuma sala encontrado")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'CINEMA_ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping()
    public ResponseEntity<Page<RoomResponseDTO>> getAllRooms(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal AuthenticatedUser user) {
        Page<RoomResponseDTO> rooms = roomService.getAllRooms(page, size, user);

        return ResponseEntity.ok().cacheControl(noCachePrivate)
                .body(rooms);
    }

    @Operation(summary = "Atualizar sala", description = "Atualiza uma sala existente")
    @ApiResponse(responseCode = "200", description = "Sala atualizada com sucesso")
    @ApiResponse(responseCode = "404", description = "Sala não encontrada")
    @ApiResponse(responseCode = "400", description = "Erro de validação")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'CINEMA_ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/{id}")
    public ResponseEntity<RoomResponseDTO> updateRoom(@PathVariable Long id, @RequestBody RoomRequestDTO roomRequestDTO, @AuthenticationPrincipal AuthenticatedUser user) {
        RoomResponseDTO updatedRoom = roomService.updateRoom(id, roomRequestDTO, user);
        return ResponseEntity.ok(updatedRoom);
    }

    @Operation(summary = "Deletar sala", description = "Inativa (soft delete) uma sala existente")
    @ApiResponse(responseCode = "204", description = "Sala inativada com sucesso")
    @ApiResponse(responseCode = "404", description = "Sala não encontrada")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'CINEMA_ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id, @AuthenticationPrincipal AuthenticatedUser user) {
        roomService.delete(id, user);
        return ResponseEntity.noContent().build();
    }

}
