CREATE TYPE user_role AS ENUM ('ADMIN', 'USER');

CREATE TYPE movie_session_status AS ENUM ('SCHEDULED', 'ACTIVE', 'FINISHED', 'CANCELED');

CREATE TYPE reservation_status AS ENUM ('RESERVED', 'CONSUMED', 'EXPIRED', 'CANCELLED');

CREATE TYPE ticket_category AS ENUM ('REGULAR', 'STUDENT', 'SENIOR');

CREATE TYPE purchase_status AS ENUM ('CREATED', 'PENDING', 'PAID', 'CANCELLED', 'REFUNDED');

CREATE TYPE payment_type AS ENUM ('PIX', 'CARTAO');

CREATE TYPE payment_status AS ENUM ('PENDING', 'APPROVED', 'REJECTED', 'CANCELLED', 'REFUNDED');


CREATE TABLE genre (
    id      BIGSERIAL    PRIMARY KEY,
    name    VARCHAR(100) NOT NULL UNIQUE
);


CREATE TABLE movie (
    id           BIGSERIAL    PRIMARY KEY,
    title        VARCHAR(255) NOT NULL,
    description  TEXT         NOT NULL,
    release_date DATE         NOT NULL,
    duration     INTEGER      NOT NULL CHECK (duration > 0),
    image_url    VARCHAR(512),
    genre_id     BIGINT       NOT NULL REFERENCES genre(id)
);


CREATE INDEX idx_movie_genre_id ON movie(genre_id);

CREATE INDEX idx_movie_release_date ON movie(release_date);


CREATE TABLE room (
    id       BIGSERIAL    PRIMARY KEY,
    number   VARCHAR(20)  NOT NULL UNIQUE,
    capacity INTEGER      NOT NULL CHECK (capacity > 0)
);


CREATE TABLE movie_session (
    id         BIGSERIAL      PRIMARY KEY,
    show_date  DATE           NOT NULL,
    start_time TIME           NOT NULL,
    end_time   TIME           NOT NULL,
    base_price NUMERIC(10, 2) NOT NULL CHECK (base_price >= 0),
    canceled   BOOLEAN        NOT NULL DEFAULT FALSE,
    room_id    BIGINT         NOT NULL REFERENCES room(id),
    movie_id   BIGINT         NOT NULL REFERENCES movie(id),

    CONSTRAINT chk_session_time CHECK (start_time < end_time)
);

CREATE INDEX idx_movie_session_movie_id ON movie_session(movie_id);

CREATE INDEX idx_movie_session_room_id ON movie_session(room_id);

CREATE INDEX idx_movie_session_show_date ON movie_session(show_date);

CREATE INDEX idx_movie_session_available ON movie_session(show_date, canceled)
    WHERE canceled = FALSE;

CREATE TABLE users (
    id              UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    username        VARCHAR(100) NOT NULL UNIQUE,
    name            VARCHAR(255),
    cpf             VARCHAR(14)  NOT NULL UNIQUE,
    email           VARCHAR(255) NOT NULL UNIQUE,
    password        VARCHAR(255) NOT NULL,
    data_joined     DATE,
    birthdate       DATE,
    failed_attempt  INTEGER      NOT NULL DEFAULT 0,
    lock_time       TIMESTAMP,
    is_locked       BOOLEAN      NOT NULL DEFAULT FALSE,
    role            user_role    NOT NULL
);

CREATE INDEX idx_users_is_locked ON users(is_locked) WHERE is_locked = TRUE;


CREATE TABLE purchase (
    id               BIGSERIAL      PRIMARY KEY,
    purchase_date    TIMESTAMP      NOT NULL,
    total_price      NUMERIC(10, 2) NOT NULL DEFAULT 0,
    idempotency_key  CHAR(36)       NOT NULL UNIQUE,
    purchase_status  purchase_status NOT NULL,
    user_id          UUID           NOT NULL REFERENCES users(id)
);

CREATE INDEX idx_purchase_user_id ON purchase(user_id);


CREATE INDEX idx_purchase_status ON purchase(purchase_status);

CREATE INDEX idx_purchase_user_status ON purchase(user_id, purchase_status);


CREATE TABLE payment (
    id              BIGSERIAL      PRIMARY KEY,
    payment_date    TIMESTAMP,
    transaction_id  BIGINT,
    version         INTEGER        NOT NULL DEFAULT 0,
    payment_method  payment_type   NOT NULL,
    payment_status  payment_status NOT NULL,
    status_detail   VARCHAR(255),
    purchase_id     BIGINT         NOT NULL UNIQUE REFERENCES purchase(id)
);

CREATE INDEX idx_payment_transaction_id ON payment(transaction_id);

CREATE INDEX idx_payment_status ON payment(payment_status);

CREATE TABLE tickets (
    id           BIGSERIAL      PRIMARY KEY,
    seat_number  INTEGER        NOT NULL,
    category     ticket_category NOT NULL,
    price        NUMERIC(10, 2) NOT NULL CHECK (price > 0),
    session_id   BIGINT         NOT NULL REFERENCES movie_session(id),
    purchase_id  BIGINT         NOT NULL REFERENCES purchase(id),

    CONSTRAINT uq_ticket_session_seat UNIQUE (session_id, seat_number)
);

CREATE INDEX idx_ticket_purchase_id ON tickets(purchase_id);

CREATE INDEX idx_ticket_session_id ON tickets(session_id);

CREATE TABLE seat_reservations (
    id           BIGSERIAL          PRIMARY KEY,
    seat_number  INTEGER            NOT NULL,
    status       reservation_status NOT NULL,
    expires_at   TIMESTAMP          NOT NULL,
    session_id   BIGINT             NOT NULL REFERENCES movie_session(id),
    user_id      UUID               NOT NULL REFERENCES users(id)
);

CREATE INDEX idx_seat_reservation_session_id ON seat_reservations(session_id);

CREATE INDEX idx_seat_reservation_user_id ON seat_reservations(user_id);

CREATE INDEX idx_seat_reservation_active ON seat_reservations(session_id, seat_number, expires_at)
    WHERE status = 'RESERVED';
