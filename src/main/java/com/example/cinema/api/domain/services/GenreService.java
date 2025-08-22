package com.example.cinema.api.domain.services;

import com.example.cinema.api.domain.entities.Genre;
import com.example.cinema.api.shared.dtos.genre.GenreRequestDTO;
import com.example.cinema.api.shared.dtos.genre.GenreResponseDTO;
import com.example.cinema.api.shared.dtos.genre.GenreUpdateDTO;
import com.example.cinema.api.shared.exceptions.GeneroJaExisteException;
import com.example.cinema.api.shared.exceptions.ResourceNotFoundException;
import com.example.cinema.api.shared.mappers.GenreMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class GenreService {

    private final com.example.cinema.api.domain.repositories.GenreRepository genreRepository;
    private final GenreMapper genreMapper;

    public GenreService(com.example.cinema.api.domain.repositories.GenreRepository genreRepository, GenreMapper genreMapper) {
        this.genreRepository = genreRepository;
        this.genreMapper = genreMapper;
    }

    @Transactional
    public GenreResponseDTO create(GenreRequestDTO dto) {
        Genre genero = genreMapper.toEntity(dto);
        return genreMapper.toDTO(genreRepository.save(genero));
    }

    @Transactional(readOnly = true)
    public GenreResponseDTO findById(Long id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gênero não encontrado"));
        return genreMapper.toDTO(genre);
    }

    @Transactional(readOnly = true)
    public Page<GenreResponseDTO> findAllPageable(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return genreRepository.findAllBy(pageable).map(genreMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<GenreResponseDTO> findByNameContainingIgnoreCase(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return genreRepository.findByNameContainingIgnoreCase(name, pageable).map(genreMapper::toDTO);
    }

    @Transactional
    public GenreResponseDTO update(Long id, GenreUpdateDTO dto) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gênero não encontrado"));

        if(genreRepository.existsByName(dto.getName()) && !Objects.equals(dto.getName(), genre.getName())){
            throw new GeneroJaExisteException("Gênero com o nome " + dto.getName() + " já existe");
        }

        genreMapper.updateEntityFromDTO(dto, genre);
        return genreMapper.toDTO(genreRepository.save(genre));

    }

}

