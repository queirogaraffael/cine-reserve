CREATE UNIQUE INDEX uq_active_seat_reservation
    ON seat_reservations (movie_session_id, seat_number)
    WHERE status = 'RESERVED';

CREATE UNIQUE INDEX uq_ticket_seat
    ON tickets (session_id, seat_number);