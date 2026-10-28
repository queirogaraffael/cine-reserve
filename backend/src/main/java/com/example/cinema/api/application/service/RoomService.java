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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.cinema.api.infrastructure.security.AuthenticatedUser;

@Service
public class RoomService {

    private final RoomRepositoryJpa roomRepositoryJpa;
    private final CinemaRepositoryJpa cinemaRepositoryJpa;
    private final RoomMapper roomMapper;
    private final CinemaAdminService cinemaAdminService;

    public RoomService(RoomRepositoryJpa roomRepositoryJpa, CinemaRepositoryJpa cinemaRepositoryJpa,
            RoomMapper roomMapper, CinemaAdminService cinemaAdminService) {
        this.roomRepositoryJpa = roomRepositoryJpa;
        this.cinemaRepositoryJpa = cinemaRepositoryJpa;
        this.roomMapper = roomMapper;
        this.cinemaAdminService = cinemaAdminService;
    }

    @Transactional
    @CachePut(value = "rooms", key = "#result.id")
    public RoomResponseDTO createRoom(RoomRequestDTO roomRequestDTO, AuthenticatedUser user) {
        cinemaAdminService.validateCinemaOwnership(user, roomRequestDTO.getCinemaId());

        Cinema cinema = cinemaRepositoryJpa.findById(roomRequestDTO.getCinemaId())
                .orElseThrow(() -> new com.example.cinema.api.domain.cinema.exception.CinemaRequiredException(
                        "Cinema não encontrado com id: " + roomRequestDTO.getCinemaId()));

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
        Room room = roomRepositoryJpa.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new RoomNotFoundException("Sala não encontrada"));
        return roomMapper.toDTO(room);
    }

    @Transactional(readOnly = true)
    public Page<RoomResponseDTO> getAllRooms(int page, int size, AuthenticatedUser user) {
        Pageable pageable = PageRequest.of(page, size);

        if (user.getCinemaId() != null) {
            return roomRepositoryJpa.findAllByCinemaIdPaginado(user.getCinemaId(), pageable);
        }

        return roomRepositoryJpa.findAllPaginado(pageable);
    }

    @Transactional
    @CachePut(value = "rooms", key = "#id")
    public RoomResponseDTO updateRoom(Long id, RoomRequestDTO roomRequestDTO, AuthenticatedUser user) {
        Room room = roomRepositoryJpa.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new RoomNotFoundException("Sala não encontrada para modificação"));

        cinemaAdminService.validateCinemaOwnership(user, room.getCinema().getId());
        cinemaAdminService.validateCinemaOwnership(user, roomRequestDTO.getCinemaId());

        if (roomRepositoryJpa.existsByNameAndCinemaId(roomRequestDTO.getName(), room.getCinema().getId())
                && !room.getName().equals(roomRequestDTO.getName())) {
            throw new RoomNameAlreadyExistsInCinemaException("Nome de sala já cadastrado neste cinema.");
        }

        room.changeName(roomRequestDTO.getName());

        return roomMapper.toDTO(roomRepositoryJpa.save(room));
    }

    @Transactional
    @CacheEvict(value = "rooms", key = "#id")
    public void delete(Long id, AuthenticatedUser user) {
        Room room = roomRepositoryJpa.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new RoomNotFoundException("Sala não encontrada"));

        cinemaAdminService.validateCinemaOwnership(user, room.getCinema().getId());

        roomRepositoryJpa.softDelete(id);
    }

}
