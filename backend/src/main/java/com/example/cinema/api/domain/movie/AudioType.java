package com.example.cinema.api.domain.movie;

public enum AudioType {
    DUBLADO("Dublado"),
    LEGENDADO("Legendado"),
    NACIONAL("Nacional");

    private final String label;

    AudioType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
