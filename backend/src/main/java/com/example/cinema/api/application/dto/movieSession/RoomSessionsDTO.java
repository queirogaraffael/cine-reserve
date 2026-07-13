package com.example.cinema.api.application.dto.movieSession;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RoomSessionsDTO {

    private Long roomId;
    private String roomName;
    private List<SessionSlotDTO> sessions;
}
