package com.example.cinema.api.application.service;

import com.example.cinema.api.domain.genre.Genre;
import com.example.cinema.api.domain.genre.exception.GenreNotFoundException;
import com.example.cinema.api.infrastructure.persistence.GenreRepositoryJpa;
import com.example.cinema.api.application.dto.genre.GenreRequestDTO;
import com.example.cinema.api.application.dto.genre.GenreResponseDTO;
import com.example.cinema.api.application.dto.genre.GenreUpdateDTO;
import com.example.cinema.api.domain.genre.exception.GenreAlreadyExistsException;
import com.example.cinema.api.application.mapper.GenreMapper;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class GenreService {

    private final GenreRepositoryJpa genreRepository;
    private final GenreMapper genreMapper;

    public GenreService(GenreRepositoryJpa genreRepository, GenreMapper genreMapper) {
        this.genreRepository = genreRepository;
        this.genreMapper = genreMapper;
    }

    @Transactional
    @CachePut(value = "genres", key = "#result.id")
    public GenreResponseDTO create(GenreRequestDTO dto) {

        Genre genero = new Genre(dto.getName());

        return genreMapper.toDTO(genreRepository.save(genero));
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "genres", key = "#id")
    public GenreResponseDTO findById(Long id) {
        Genre genre = genreRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new GenreNotFoundException("Gênero não encontrado"));
        return genreMapper.toDTO(genre);
    }

    @Transactional(readOnly = true)
    public Page<GenreResponseDTO> findAllPageable(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return genreRepository.findAllByActiveTrue(pageable).map(genreMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<GenreResponseDTO> findByNameContainingIgnoreCase(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return genreRepository.findByNameContainingIgnoreCaseAndActiveTrue(name, pageable).map(genreMapper::toDTO);
    }

    @Transactional
    @CachePut(value = "genres", key = "#id")
    public GenreResponseDTO update(Long id, GenreUpdateDTO dto) {
        Genre genre = genreRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new GenreNotFoundException("Gênero não encontrado"));

        if(genreRepository.existsByName(dto.getName()) && !Objects.equals(dto.getName(), genre.getName())){
            throw new GenreAlreadyExistsException("Gênero com o nome " + dto.getName() + " já existe");
        }

        genreMapper.updateEntityFromDTO(dto, genre);
        return genreMapper.toDTO(genreRepository.save(genre));

    }

    @Transactional
    @CacheEvict(value = "genres", key = "#id")
    public void delete(Long id) {
        if (!genreRepository.existsByIdAndActiveTrue(id)) {
            throw new GenreNotFoundException("Gênero não encontrado");
        }
        genreRepository.softDelete(id);
    }

}

