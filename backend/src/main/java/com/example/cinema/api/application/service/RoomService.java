package com.example.cinema.api.application.service;

import com.example.cinema.api.domain.cinema.Cinema;
import com.example.cinema.api.domain.room.Room;
import com.example.cinema.api.domain.room.exception.RoomNotFoundException;
import com.example.cinema.api.domain.room.exception.RoomNameAlreadyExistsInCinemaException;
import com.example.cinema.api.infrastructure.persistence.CinemaRepositoryJpa;
import com.example.cinema.api.infrastructure.persistence.RoomRepositoryJpa;
import com.example.cinema.api.application.dto.room.RoomRequestDTO;
import com.example.cinema.api.application.dto.room.RoomResponseDTO;
import com.example.cinema.api.application.mapper.RoomMapper;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoomService {

    private final RoomRepositoryJpa roomRepositoryJpa;
    private final CinemaRepositoryJpa cinemaRepositoryJpa;
    private final RoomMapper roomMapper;

    public RoomService(RoomRepositoryJpa roomRepositoryJpa, CinemaRepositoryJpa cinemaRepositoryJpa, RoomMapper roomMapper) {
        this.roomRepositoryJpa = roomRepositoryJpa;
        this.cinemaRepositoryJpa = cinemaRepositoryJpa;
        this.roomMapper = roomMapper;
    }

    @Transactional
    @CachePut(value = "rooms", key = "#result.id")
    public RoomResponseDTO createRoom(RoomRequestDTO roomRequestDTO) {
        Cinema cinema = cinemaRepositoryJpa.findById(roomRequestDTO.getCinemaId())
                .orElseThrow(() -> new com.example.cinema.api.domain.cinema.exception.CinemaRequiredException("Cinema não encontrado com id: " + roomRequestDTO.getCinemaId()));

        if (roomRepositoryJpa.existsByNameAndCinemaId(roomRequestDTO.getName(), roomRequestDTO.getCinemaId())) {
            throw new RoomNameAlreadyExistsInCinemaException("Nome de sala já cadastrado neste cinema.");
        }

        Room room = new Room(roomRequestDTO.getName(), cinema);

        room = roomRepositoryJpa.save(room);
        return roomMapper.toDTO(room);
    }

    @Transactional(readOnly = true)
    @CachePut(value = "rooms", key = "#id")
    public RoomResponseDTO getRoomById(Long id) {
        Room room = roomRepositoryJpa.findById(id)
                .orElseThrow(() -> new RoomNotFoundException("Sala não encontrada"));
        return roomMapper.toDTO(room);
    }

    @Transactional(readOnly = true)
    public Page<RoomResponseDTO> getAllRooms(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return roomRepositoryJpa.findAllPaginado(pageable);
    }

    @Transactional
    @CachePut(value = "rooms", key = "#id")
    public RoomResponseDTO updateRoom(Long id, RoomRequestDTO roomRequestDTO) {
        Room room = roomRepositoryJpa.findById(id)
                .orElseThrow(() -> new RoomNotFoundException("Sala não encontrada para modificação"));

        if (roomRepositoryJpa.existsByNameAndCinemaId(roomRequestDTO.getName(), room.getCinema().getId())
                && !room.getName().equals(roomRequestDTO.getName())) {
            throw new RoomNameAlreadyExistsInCinemaException("Nome de sala já cadastrado neste cinema.");
        }

        room.changeName(roomRequestDTO.getName());


        return roomMapper.toDTO(roomRepositoryJpa.save(room));
    }

}
