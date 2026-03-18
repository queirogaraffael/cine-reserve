package com.example.cinema.api.application.service;

import com.example.cinema.api.domain.room.Room;
import com.example.cinema.api.domain.room.exception.RoomNotFoundException;
import com.example.cinema.api.infrastructure.persistence.RoomRepositoryJpa;
import com.example.cinema.api.application.dto.room.RoomRequestDTO;
import com.example.cinema.api.application.dto.room.RoomResponseDTO;

import com.example.cinema.api.domain.room.exception.RoomNumberAlreadyExistsException;
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
    private final RoomMapper roomMapper;

    public RoomService(RoomRepositoryJpa roomRepositoryJpa, RoomMapper roomMapper) {
        this.roomRepositoryJpa = roomRepositoryJpa;
        this.roomMapper = roomMapper;
    }

    @Transactional
    @CachePut(value = "rooms", key = "#result.id")
    public RoomResponseDTO createRoom(RoomRequestDTO roomRequestDTO) {

        if(roomRepositoryJpa.existsByNumber(roomRequestDTO.getNumber())) {
            throw new RoomNumberAlreadyExistsException("Número de sala já cadastrado");
        }

        Room room = new Room(roomRequestDTO.getNumber(), roomRequestDTO.getCapacity());

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

        if (roomRepositoryJpa.existsByNumber(roomRequestDTO.getNumber()) && !room.getNumber().equals(roomRequestDTO.getNumber())) {
            throw new RoomNumberAlreadyExistsException("Número de sala já cadastrado");
        }

        room.changeNumber(roomRequestDTO.getNumber());
        room.changeCapacity(roomRequestDTO.getCapacity());

        return roomMapper.toDTO(roomRepositoryJpa.save(room));
    }

}
