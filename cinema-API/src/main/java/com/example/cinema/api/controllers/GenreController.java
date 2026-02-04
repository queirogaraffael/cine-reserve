package com.example.cinema.api.controllers;

import com.example.cinema.api.domain.services.GenreService;
import com.example.cinema.api.shared.dtos.genre.GenreRequestDTO;
import com.example.cinema.api.shared.dtos.genre.GenreResponseDTO;
import com.example.cinema.api.shared.dtos.genre.GenreUpdateDTO;
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
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.concurrent.TimeUnit;

@Tag(name = "Genres")
@RestController
@RequestMapping("/api/genres")
public class GenreController {

    private final GenreService genreService;
    private final CacheControl cacheControl;

    public GenreController(GenreService genreService, @Value("${cache.ttl}") long cacheTtl) {
        this.genreService = genreService;
        this.cacheControl = CacheControl.maxAge(cacheTtl, TimeUnit.SECONDS).cachePublic();
    }

    @Operation(summary = "Criar novo gênero", description = "Cria um novo gênero")
    @ApiResponse(responseCode = "201", description = "Gênero criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Erro de validação")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping()
    public ResponseEntity<GenreResponseDTO> createGenre(@RequestBody @Valid GenreRequestDTO dto) {

        GenreResponseDTO responseDTO = genreService.create(dto);

         URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                 .buildAndExpand(responseDTO.getId()).toUri();
         return ResponseEntity.created(uri).body(responseDTO);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar gênero por ID", description = "Retorna um gênero específico")
    @ApiResponse(responseCode = "200", description = "Gênero encontrado")
    @ApiResponse(responseCode = "404", description = "Gênero não encontrado")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    public ResponseEntity<GenreResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok().cacheControl(cacheControl).body(genreService.findById(id));
    }

    @GetMapping()
    @Operation(summary = "Buscar todos os gêneros paginados", description = "Retorna uma lista paginada de gêneros")
    @ApiResponse(responseCode = "200", description = "Lista de gêneros encontrada")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    public ResponseEntity<?> findAllPageable(@RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok().cacheControl(CacheControl.noCache().cachePrivate()).body(genreService.findAllPageable(page, size));
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar gêneros por nome paginados", description = "Retorna uma lista paginada de gêneros filtrados pelo nome")
    @ApiResponse(responseCode = "200", description = "Lista de gêneros encontrada")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    public ResponseEntity<Page<GenreResponseDTO>> findByNameContainingIgnoreCase(@RequestParam String name,
                                                                                 @RequestParam(defaultValue = "0") int page,
                                                                                 @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok().cacheControl(CacheControl.noCache().cachePrivate()).body(genreService.findByNameContainingIgnoreCase(name, page, size));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar gênero", description = "Atualiza um gênero existente")
    @ApiResponse(responseCode = "200", description = "Gênero atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Gênero não encontrado")
    @ApiResponse(responseCode = "400", description = "Erro de validação")
    @ApiResponse(responseCode = "409", description = "Gênero já existe com esse nome")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    public ResponseEntity<GenreResponseDTO> update(@PathVariable Long id,
                                                   @RequestBody @Valid GenreUpdateDTO dto) {
        return ResponseEntity.ok(genreService.update(id, dto));
    }

}
