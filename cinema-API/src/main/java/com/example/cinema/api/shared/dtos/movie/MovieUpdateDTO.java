package com.example.cinema.api.shared.dtos.movie;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieUpdateDTO {

    private String title;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    private String imageUrl;
    private Long genreId;

}
