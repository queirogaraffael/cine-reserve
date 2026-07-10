package com.example.cinema.api.domain.movie;

public enum MovieFormat {
    F2D("2D"),
    F3D("3D");

    private final String label;

    MovieFormat(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
