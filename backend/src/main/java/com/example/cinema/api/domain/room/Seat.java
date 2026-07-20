package com.example.cinema.api.domain.room;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "seats")
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(nullable = false, length = 1)
    private String rowLetter;

    @Column(nullable = false)
    private Integer columnNumber;

    @Column(nullable = false, length = 10)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatType type;

    @Column(nullable = false)
    private boolean active = true;

    public Seat(Room room, String rowLetter, Integer columnNumber, SeatType type) {
        this.room = room;
        this.rowLetter = rowLetter;
        this.columnNumber = columnNumber;
        this.code = rowLetter + columnNumber;
        this.type = type;
        this.active = true;
    }
}
