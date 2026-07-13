package com.example.cinema.api.application.dto.movieSession;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class DaySessionsDTO {

    private LocalDate date;
    private String dayOfWeek;
    private List<RoomSessionsDTO> rooms;
}
