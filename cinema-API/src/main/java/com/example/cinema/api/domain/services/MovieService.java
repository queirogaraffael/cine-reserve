package com.example.cinema.api.domain.services;

import com.example.cinema.api.domain.entities.Genre;
import com.example.cinema.api.domain.entities.Movie;
import com.example.cinema.api.infrastructure.repositories.GenreRepository;
import com.example.cinema.api.infrastructure.repositories.MovieRepository;
import com.example.cinema.api.shared.dtos.movie.MovieRequestDTO;
import com.example.cinema.api.shared.dtos.movie.MovieResponseDTO;
import com.example.cinema.api.shared.dtos.movie.MovieUpdateDTO;
import com.example.cinema.api.shared.exceptions.ResourceNotFoundException;
import com.example.cinema.api.shared.mappers.MovieMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final MovieMapper movieMapper;

    public MovieService(MovieRepository movieRepository, GenreRepository genreRepository, MovieMapper movieMapper) {
        this.movieRepository = movieRepository;
        this.genreRepository = genreRepository;
        this.movieMapper = movieMapper;
    }

    @Transactional
    public MovieResponseDTO createMovie(Long genreId, MovieRequestDTO dto) {
        Genre genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new ResourceNotFoundException("Gênero não encontrado"));

        Movie movie = movieMapper.toEntity(dto);
        movie.setGenre(genre);
        Movie movieSaved = movieRepository.save(movie);
        return movieMapper.toDTO(movieSaved);
    }

    @Transactional(readOnly = true)
    public MovieResponseDTO findById(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Filme não encontrado"));
        return movieMapper.toDTO(movie);
    }

    @Transactional(readOnly = true)
    public Page<MovieResponseDTO> findAllPageable(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return movieRepository.findAllPaginado(pageable);
    }

    @Transactional(readOnly = true)
    public Page<MovieResponseDTO> findByGenreId(Long genreId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return movieRepository.findByGenreId(genreId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<MovieResponseDTO> findByTitleAndGenreId(String title, Long genreId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return movieRepository.findByTitleContainingAndGenreId(title, genreId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<MovieResponseDTO> findByTitleContainingIgnoreCase(String title, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return movieRepository.findByTitleContainingIgnoreCase(title, pageable).map(movieMapper::projectionToDTO);
    }

    @Transactional
    public MovieResponseDTO updateMovie(Long idMovie, MovieUpdateDTO dto) {

        Movie movie = movieRepository.findById(idMovie)
                .orElseThrow(() -> new ResourceNotFoundException("Filme não encontrado"));

        Genre genre = genreRepository.findById(dto.getGenreId())
                .orElseThrow(() -> new ResourceNotFoundException("Gênero não encontrado"));

        movie.setGenre(genre);

        movieMapper.updateEntityFromDTO(dto, movie);
        Movie movieUpdated = movieRepository.save(movie);

        return movieMapper.toDTO(movieUpdated);
    }


}
