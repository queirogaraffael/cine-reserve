package com.example.cinema.api.infrastructure.repositories;

import com.example.cinema.api.domain.entities.Movie;
import com.example.cinema.api.infrastructure.repositories.projection.MovieResponseDTOProjection;
import com.example.cinema.api.shared.dtos.movie.MovieResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    @Query(
            value = "SELECT new com.example.cinema.api.shared.dtos.movie.MovieResponseDTO(m.id, m.title, m.description, m.releaseDate, m.duration, m.imageUrl) FROM Movie m",
            countQuery = "SELECT count(m) FROM Movie m")
    Page<MovieResponseDTO> findAllPaginado(Pageable pageable);


    Page<MovieResponseDTOProjection> findByTitleContainingIgnoreCase(String title, Pageable pageable);


    @Query(
            value = "SELECT new com.example.cinema.api.shared.dtos.movie.MovieResponseDTO(m.id, m.title, m.description, m.releaseDate, m.duration, m.imageUrl) FROM Movie m WHERE m.genre.id = :genreId",
            countQuery = "SELECT count(m) FROM Movie m WHERE m.genre.id = :genreId")
    Page<MovieResponseDTO> findByGenreId(@Param("genreId") Long genreId, Pageable pageable);


    @Query(
            value = "SELECT new com.example.cinema.api.shared.dtos.movie.MovieResponseDTO(m.id, m.title, m.description, m.releaseDate, m.duration, m.imageUrl) " +
                    "FROM Movie m WHERE LOWER(m.title) LIKE LOWER(CONCAT('%', :title, '%')) AND m.genre.id = :genreId",
            countQuery = "SELECT count(m) FROM Movie m WHERE LOWER(m.title) LIKE LOWER(CONCAT('%', :title, '%')) AND m.genre.id = :genreId"
    )
    Page<MovieResponseDTO> findByTitleContainingAndGenreId(@Param("title") String title, @Param("genreId") Long genreId, Pageable pageable);


    boolean existsById(Long id);
}
