package com.example.cinema.api.infrastructure.startup;

import com.example.cinema.api.domain.cinema.Cinema;
import com.example.cinema.api.domain.room.Room;
import com.example.cinema.api.domain.room.Seat;
import com.example.cinema.api.domain.room.SeatType;
import com.example.cinema.api.domain.ticket.TicketCategory;
import com.example.cinema.api.domain.ticket.TicketType;
import com.example.cinema.api.infrastructure.persistence.CinemaRepositoryJpa;
import com.example.cinema.api.infrastructure.persistence.RoomRepositoryJpa;
import com.example.cinema.api.infrastructure.persistence.TicketTypeRepositoryJpa;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CinemaRoomInitializer implements CommandLineRunner {

    private final RoomRepositoryJpa roomRepositoryJpa;
    private final CinemaRepositoryJpa cinemaRepositoryJpa;
    private final TicketTypeRepositoryJpa ticketTypeRepositoryJpa;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (roomRepositoryJpa.count() == 0) {
            log.info("Inicializando Cinema, Sala, Assentos e Tipos de Ingresso...");
            
            Cinema cinema = new Cinema("Cine Reserve Premium", "São Paulo", "SP", "http://logo.com/logo.png");
            cinemaRepositoryJpa.save(cinema);

            Room room = new Room("Sala 1 - IMAX", cinema);

            char[] rows = {'A', 'B', 'C', 'D', 'E', 'F', 'G'};
            for (char row : rows) {
                for (int col = 1; col <= 18; col++) {
                    SeatType type = SeatType.REGULAR;
                    if (row == 'A' && (col <= 2 || col >= 17)) type = SeatType.WHEELCHAIR;
                    if (row == 'G' && (col >= 5 && col <= 14)) type = SeatType.VIP;

                    Seat seat = new Seat(room, String.valueOf(row), col, type);
                    room.addSeat(seat);
                }
            }
            roomRepositoryJpa.save(room);

            TicketType inteira = new TicketType("Inteira", BigDecimal.valueOf(40.00), TicketCategory.INTEIRA, "Ingresso padrao");
            TicketType meia = new TicketType("Meia Entrada", BigDecimal.valueOf(20.00), TicketCategory.MEIA_ENTRADA, "Estudantes, idosos e professores");
            TicketType promocional = new TicketType("Promocional", BigDecimal.valueOf(15.00), TicketCategory.PROMOCIONAL, "Promocao especial");
            TicketType cortesia = new TicketType("Cortesia", BigDecimal.ZERO, TicketCategory.CORTESIA, "Entrada gratuita");

            ticketTypeRepositoryJpa.saveAll(List.of(inteira, meia, promocional, cortesia));

            log.info("Inicializacao completa!");
        }
    }
}
