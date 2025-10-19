package com.example.cinema.api.domain.entities;


import com.example.cinema.api.domain.enums.MovieSessionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieSession {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private LocalDate showDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private BigDecimal basePrice;

    private boolean canceled = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room cinemaRoom;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @OneToMany(mappedBy = "movieSession", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Ticket> tickets = new ArrayList<>();

    // Método calcula o Status dinamicamene: SCHEDULED, ACTIVE, FINISHED ou CANCELED.
    @Transient
    public MovieSessionStatus getStatus() {
        if (canceled) {
            return MovieSessionStatus.CANCELED;
        }

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        if (today.isBefore(showDate) || (today.isEqual(showDate) && now.isBefore(startTime))) {
            return MovieSessionStatus.SCHEDULED;
        }
        if (today.isEqual(showDate) && (now.isAfter(startTime) || now.equals(startTime)) && now.isBefore(endTime)) {
            return MovieSessionStatus.ACTIVE;
        }
        return MovieSessionStatus.FINISHED;
    }


    // Metodo indica se MovieSession está aberta para a venda de ingressos.
    @Transient
    public boolean isAvailableForPurchase() {
        return !canceled && getStatus() == MovieSessionStatus.SCHEDULED;
    }
}
