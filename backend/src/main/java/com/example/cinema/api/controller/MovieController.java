package com.example.cinema.api.controller;

import com.example.cinema.api.application.service.MovieService;
import com.example.cinema.api.application.dto.movie.MovieRequestDTO;
import com.example.cinema.api.application.dto.movie.MovieResponseDTO;
import com.example.cinema.api.application.dto.movie.MovieUpdateDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@Tag(name = "Movies")
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @Operation(summary = "Criar novo filme", description = "Cria um novo filme")
    @ApiResponse(responseCode = "201", description = "Filme criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Erro de validação")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping()
    public ResponseEntity<MovieResponseDTO> createMovie(@RequestBody @Valid MovieRequestDTO dto) {
        MovieResponseDTO movieResponseDTO = movieService.createMovie(dto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(movieResponseDTO.getId()).toUri();

        return ResponseEntity.created(uri).body(movieResponseDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Buscar filme por ID", description = "Busca um filme pelo ID")
    @ApiResponse(responseCode = "200", description = "Filme encontrado")
    @ApiResponse(responseCode = "404", description = "Filme não encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<MovieResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok()
                .body(movieService.findById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Buscar todos os filmes paginados", description = "Busca todos os filmes com paginação")
    @ApiResponse(responseCode = "200", description = "Lista de filmes encontrada")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @GetMapping()
    public ResponseEntity<Page<MovieResponseDTO>> findAllPageable(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok()
                .body(movieService.findAllPageable(page, size));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Busca paginada de filmes por título", description = "Busca filmes pelo título")
    @ApiResponse(responseCode = "200", description = "Lista de filmes encontrada")
    @ApiResponse(responseCode = "404", description = "Filme não encontrado")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @GetMapping("/search")
    public ResponseEntity<Page<MovieResponseDTO>> findByTitleContainingIgnoreCase(@RequestParam String title, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok()
                .body(movieService.findByTitleContainingIgnoreCase(title, page, size));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Busca paginada de filmes por gênero", description = "Busca filmes pelo gênero")
    @ApiResponse(responseCode = "200", description = "Lista de filmes encontrada")
    @ApiResponse(responseCode = "404", description = "Filme não encontrado")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @GetMapping("/genre/{genreId}")
    public ResponseEntity<Page<MovieResponseDTO>> findByGenreId(@PathVariable Long genreId, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok()
                .body(movieService.findByGenreId(genreId, page, size));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Busca paginada de filmes por título e gênero", description = "Busca filmes pelo título e gênero")
    @ApiResponse(responseCode = "200", description = "Lista de filmes encontrada")
    @ApiResponse(responseCode = "404", description = "Filme não encontrado")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @GetMapping("/search/genre/{genreId}")
    public ResponseEntity<Page<MovieResponseDTO>> findByTitleAndGenreId(@RequestParam String title, @PathVariable Long genreId, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok()
                .body(movieService.findByTitleAndGenreId(title, genreId, page, size));
    }

    @Operation(summary = "Atualizar filme", description = "Atualiza um filme existente")
    @ApiResponse(responseCode = "200", description = "Filme atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Filme não encontrado")
    @ApiResponse(responseCode = "400", description = "Erro de validação")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/{id}")
    public ResponseEntity<MovieResponseDTO> updateMovie(@PathVariable Long id, @RequestBody @Valid MovieUpdateDTO dto) {
        MovieResponseDTO movieResponseDTO = movieService.updateMovie(id, dto);
        return ResponseEntity.ok(movieResponseDTO);
    }

    @Operation(summary = "Deletar filme", description = "Inativa (soft delete) um filme existente")
    @ApiResponse(responseCode = "204", description = "Filme inativado com sucesso")
    @ApiResponse(responseCode = "404", description = "Filme não encontrado")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        movieService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
