package com.example.cinema.api.application.dto.movieSession;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class SessionSlotDTO {

    private Long sessionId;
    private LocalTime startTime;
    private String status;
}
