package com.example.cinema.api.infrastructure.persistence;

import com.example.cinema.api.domain.movie.Movie;
import com.example.cinema.api.infrastructure.persistence.projection.MovieResponseDTOProjection;
import com.example.cinema.api.application.dto.movie.MovieResponseDTO;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MovieRepositoryJpa extends JpaRepository<Movie, Long> {

    @Query(
            value = "SELECT new com.example.cinema.api.application.dto.movie.MovieResponseDTO(m.id, m.title, m.description, m.releaseDate, m.duration, m.imageUrl) FROM Movie m WHERE m.active = true",
            countQuery = "SELECT count(m) FROM Movie m WHERE m.active = true")
    Page<MovieResponseDTO> findAllPaginado(Pageable pageable);


    @Query(
            value = "SELECT new com.example.cinema.api.application.dto.movie.MovieResponseDTO(m.id, m.title, m.description, m.releaseDate, m.duration, m.imageUrl) FROM Movie m WHERE m.active = true AND m.inTheaters = true",
            countQuery = "SELECT count(m) FROM Movie m WHERE m.active = true AND m.inTheaters = true")
    Page<MovieResponseDTO> findAvailableMovies(Pageable pageable);

    Page<MovieResponseDTOProjection> findByTitleContainingIgnoreCaseAndActiveTrue(String title, Pageable pageable);


    @Query(
            value = "SELECT new com.example.cinema.api.application.dto.movie.MovieResponseDTO(m.id, m.title, m.description, m.releaseDate, m.duration, m.imageUrl) FROM Movie m WHERE m.genre.id = :genreId AND m.active = true",
            countQuery = "SELECT count(m) FROM Movie m WHERE m.genre.id = :genreId AND m.active = true")
    Page<MovieResponseDTO> findByGenreId(@Param("genreId") Long genreId, Pageable pageable);


    @Query(
            value = "SELECT new com.example.cinema.api.application.dto.movie.MovieResponseDTO(m.id, m.title, m.description, m.releaseDate, m.duration, m.imageUrl) " +
                    "FROM Movie m WHERE LOWER(m.title) LIKE LOWER(CONCAT('%', :title, '%')) AND m.genre.id = :genreId AND m.active = true",
            countQuery = "SELECT count(m) FROM Movie m WHERE LOWER(m.title) LIKE LOWER(CONCAT('%', :title, '%')) AND m.genre.id = :genreId AND m.active = true"
    )
    Page<MovieResponseDTO> findByTitleContainingAndGenreId(@Param("title") String title, @Param("genreId") Long genreId, Pageable pageable);

    boolean existsByIdAndActiveTrue(@NonNull Long id);

    Optional<Movie> findByIdAndActiveTrue(Long id);

    @Modifying
    @Query("UPDATE Movie m SET m.active = false WHERE m.id = :id")
    void softDelete(@Param("id") Long id);
}
