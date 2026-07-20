package com.example.cinema.api.application.dto.seat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeatDTO {
    private Long id;
    private String code;
    private String rowLetter;
    private Integer columnNumber;
    private String type;
    private boolean available;
}
