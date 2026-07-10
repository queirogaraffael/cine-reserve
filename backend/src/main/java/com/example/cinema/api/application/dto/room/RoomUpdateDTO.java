package com.example.cinema.api.application.dto.room;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomUpdateDTO {

    private String name;
    private int capacity;

}
