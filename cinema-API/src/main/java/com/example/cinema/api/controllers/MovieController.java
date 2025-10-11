package com.example.cinema.api.controllers;

import com.example.cinema.api.shared.dtos.movie.MovieRequestDTO;
import com.example.cinema.api.shared.dtos.movie.MovieResponseDTO;
import com.example.cinema.api.shared.dtos.movie.MovieUpdateDTO;
import com.example.cinema.api.domain.services.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movies")
@EnableMethodSecurity
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
    @PostMapping("/{genreId}")
    public ResponseEntity<MovieResponseDTO> createMovie(@RequestBody @Valid MovieRequestDTO dto, @PathVariable Long genreId) {
        MovieResponseDTO movieResponseDTO = movieService.createMovie(genreId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(movieResponseDTO);
    }

    @Operation(summary = "Buscar filme por ID", description = "Busca um filme pelo ID")
    @ApiResponse(responseCode = "200", description = "Filme encontrado")
    @ApiResponse(responseCode = "404", description = "Filme não encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<MovieResponseDTO> findById(@PathVariable Long id) {
        MovieResponseDTO movieResponseDTO = movieService.findById(id);
        return ResponseEntity.ok(movieResponseDTO);
    }

    @Operation(summary = "Buscar todos os filmes paginados", description = "Busca todos os filmes com paginação")
    @ApiResponse(responseCode = "200", description = "Lista de filmes encontrada")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @GetMapping()
    public ResponseEntity<Page<MovieResponseDTO>> findAllPageable(@RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(movieService.findAllPageable(page, size));
    }

    @Operation(summary = "Busca paginada de filmes por título", description = "Busca filmes pelo título")
    @ApiResponse(responseCode = "200", description = "Lista de filmes encontrada")
    @ApiResponse(responseCode = "404", description = "Filme não encontrado")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @GetMapping("/search")
    public ResponseEntity<Page<MovieResponseDTO>> findByTitleContainingIgnoreCase(@RequestParam String title,
                                                                                   @RequestParam(defaultValue = "0") int page,
                                                                                   @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(movieService.findByTitleContainingIgnoreCase(title, page, size));
    }

    @Operation(summary = "Busca paginada de filmes por gênero", description = "Busca filmes pelo gênero")
    @ApiResponse(responseCode = "200", description = "Lista de filmes encontrada")
    @ApiResponse(responseCode = "404", description = "Filme não encontrado")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @GetMapping("/genre/{genreId}")
    public ResponseEntity<Page<MovieResponseDTO>> findByGenreId(@PathVariable Long genreId,
                                                                @RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(movieService.findByGenreId(genreId, page, size));
    }

    @Operation(summary = "Busca paginada de filmes por título e gênero", description = "Busca filmes pelo título e gênero")
    @ApiResponse(responseCode = "200", description = "Lista de filmes encontrada")
    @ApiResponse(responseCode = "404", description = "Filme não encontrado")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @GetMapping("/search/genre/{genreId}")
    public ResponseEntity<Page<MovieResponseDTO>> findByTitleAndGenreId(@RequestParam String title,
                                                                        @PathVariable Long genreId,
                                                                        @RequestParam(defaultValue = "0") int page,
                                                                        @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(movieService.findByTitleAndGenreId(title, genreId, page, size));
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

}
