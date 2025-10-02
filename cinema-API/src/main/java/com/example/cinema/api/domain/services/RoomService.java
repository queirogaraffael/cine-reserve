package com.example.cinema.api.domain.services;

import com.example.cinema.api.domain.entities.Room;
import com.example.cinema.api.infrastructure.repositories.RoomRepository;
import com.example.cinema.api.shared.dtos.room.RoomRequestDTO;
import com.example.cinema.api.shared.dtos.room.RoomResponseDTO;

import com.example.cinema.api.shared.exceptions.NumeroDeQuartoJaCadastradoException;
import com.example.cinema.api.shared.exceptions.ResourceNotFoundException;
import com.example.cinema.api.shared.mappers.RoomMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;

    public RoomService(RoomRepository roomRepository, RoomMapper roomMapper) {
        this.roomRepository = roomRepository;
        this.roomMapper = roomMapper;
    }

    @Transactional
    public RoomResponseDTO createRoom(RoomRequestDTO roomRequestDTO) {

        if(roomRepository.existsByNumber(roomRequestDTO.getNumber())) {
            throw new NumeroDeQuartoJaCadastradoException("Número de sala já cadastrado");
        }

        Room room = roomMapper.toEntity(roomRequestDTO);
        room = roomRepository.save(room);
        return roomMapper.toDTO(room);
    }

    @Transactional(readOnly = true)
    public RoomResponseDTO getRoomById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sala não encontrada"));
        return roomMapper.toDTO(room);
    }

    @Transactional(readOnly = true)
    public Page<RoomResponseDTO> getAllRooms(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return roomRepository.findAllPaginado(pageable);
    }

    @Transactional
    public RoomResponseDTO updateRoom(Long id, RoomRequestDTO roomRequestDTO) {

        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sala não encontrada para modificação"));

        if (roomRepository.existsByNumber(roomRequestDTO.getNumber()) && !room.getNumber().equals(roomRequestDTO.getNumber())) {
            throw new NumeroDeQuartoJaCadastradoException("Número de sala já cadastrado");
        }

        roomMapper.updateEntityFromDTO(roomRequestDTO, room);
        return roomMapper.toDTO(roomRepository.save(room));
    }

}
