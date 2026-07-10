package com.example.cinema.api.domain.movie;

public enum MovieRating {
    LIVRE("Livre"),
    A10("10 anos"),
    A12("12 anos"),
    A14("14 anos"),
    A16("16 anos"),
    A18("18 anos");

    private final String description;

    MovieRating(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
