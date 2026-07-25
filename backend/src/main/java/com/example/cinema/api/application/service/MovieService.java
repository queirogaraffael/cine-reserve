package com.example.cinema.api.application.service;

import com.example.cinema.api.domain.genre.Genre;
import com.example.cinema.api.domain.genre.exception.GenreNotFoundException;
import com.example.cinema.api.domain.movie.Movie;
import com.example.cinema.api.domain.movie.exception.MovieNotFoundException;
import com.example.cinema.api.infrastructure.persistence.GenreRepositoryJpa;
import com.example.cinema.api.infrastructure.persistence.MovieRepositoryJpa;
import com.example.cinema.api.application.dto.movie.MovieRequestDTO;
import com.example.cinema.api.application.dto.movie.MovieResponseDTO;
import com.example.cinema.api.application.dto.movie.MovieUpdateDTO;
import com.example.cinema.api.application.mapper.MovieMapper;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MovieService {

    private final MovieRepositoryJpa movieRepositoryJpa;
    private final GenreRepositoryJpa genreRepository;
    private final MovieMapper movieMapper;

    public MovieService(MovieRepositoryJpa movieRepositoryJpa, GenreRepositoryJpa genreRepository, MovieMapper movieMapper) {
        this.movieRepositoryJpa = movieRepositoryJpa;
        this.genreRepository = genreRepository;
        this.movieMapper = movieMapper;
    }

    @Transactional
    @CachePut(value = "movies", key = "#result.id")
    public MovieResponseDTO createMovie(MovieRequestDTO dto) {
        Genre genre = genreRepository.findByIdAndActiveTrue(dto.getGenreId()).orElseThrow(() -> new GenreNotFoundException("Gênero não encontrado"));

        Movie movie = new Movie(dto.getTitle(), dto.getDescription(), dto.getReleaseDate(), dto.getDuration(), dto.getImageUrl(), genre, dto.getRating(), dto.isPreRelease());

        Movie movieSaved = movieRepositoryJpa.save(movie);
        return movieMapper.toDTO(movieSaved);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "movies", key = "#id")
    public MovieResponseDTO findById(Long id) {
        Movie movie = movieRepositoryJpa.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new MovieNotFoundException("Filme não encontrado"));
        return movieMapper.toDTO(movie);
    }

    @Transactional(readOnly = true)
    public Page<MovieResponseDTO> findAllPageable(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return movieRepositoryJpa.findAllPaginado(pageable);
    }

    @Transactional(readOnly = true)
    public Page<MovieResponseDTO> findByGenreId(Long genreId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return movieRepositoryJpa.findByGenreId(genreId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<MovieResponseDTO> findByTitleAndGenreId(String title, Long genreId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return movieRepositoryJpa.findByTitleContainingAndGenreId(title, genreId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<MovieResponseDTO> findByTitleContainingIgnoreCase(String title, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return movieRepositoryJpa.findByTitleContainingIgnoreCaseAndActiveTrue(title, pageable).map(movieMapper::projectionToDTO);
    }

    @Transactional
    @CachePut(value = "movies", key = "#idMovie")
    public MovieResponseDTO updateMovie(Long idMovie, MovieUpdateDTO dto) {
        Movie movie = movieRepositoryJpa.findByIdAndActiveTrue(idMovie)
                .orElseThrow(() -> new MovieNotFoundException("Filme não encontrado"));

        Genre genre = genreRepository.findByIdAndActiveTrue(dto.getGenreId())
                .orElseThrow(() -> new GenreNotFoundException("Gênero não encontrado"));

        movie.setGenre(genre);

        if (dto.getRating() != null) {
            movie.setRating(dto.getRating());
        }
        if (dto.getInTheaters() != null) {
            if (dto.getInTheaters()) {
                movie.putInTheaters();
            } else {
                movie.takeOffTheaters();
            }
        }
        if (dto.getPreRelease() != null) {
            movie.setPreReleaseStatus(dto.getPreRelease());
        }

        movieMapper.updateEntityFromDTO(dto, movie);
        Movie movieUpdated = movieRepositoryJpa.save(movie);

        return movieMapper.toDTO(movieUpdated);
    }


    @Transactional
    @CacheEvict(value = "movies", key = "#id")
    public void delete(Long id) {
        if (!movieRepositoryJpa.existsByIdAndActiveTrue(id)) {
            throw new MovieNotFoundException("Filme não encontrado");
        }
        movieRepositoryJpa.softDelete(id);
    }

}
