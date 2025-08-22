package com.example.cinema.api.domain.repositories.projection;

import java.time.LocalDate;

public interface MovieResponseDTOProjection {

    Long getId();
    String getTitle();
    String getDescription();
    LocalDate getReleaseDate();
    int getDuration();
    String getImageUrl();
}
