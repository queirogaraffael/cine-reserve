CREATE TABLE genre (
    id      BIGSERIAL    PRIMARY KEY,
    name    VARCHAR(100) NOT NULL UNIQUE,
    active  BOOLEAN      NOT NULL DEFAULT TRUE
);


CREATE TABLE movie (
    id           BIGSERIAL    PRIMARY KEY,
    title        VARCHAR(255) NOT NULL,
    description  TEXT         NOT NULL,
    release_date DATE         NOT NULL,
    duration     INTEGER      NOT NULL CHECK (duration > 0),
    image_url    VARCHAR(512),
    rating       VARCHAR(10)  NOT NULL,
    in_theaters  BOOLEAN      NOT NULL DEFAULT TRUE,
    pre_release  BOOLEAN      NOT NULL DEFAULT FALSE,
    active       BOOLEAN      NOT NULL DEFAULT TRUE,
    genre_id     BIGINT       NOT NULL REFERENCES genre(id)
);


CREATE INDEX idx_movie_genre_id ON movie(genre_id);

CREATE INDEX idx_movie_release_date ON movie(release_date);


CREATE TABLE cinema (
    id       BIGSERIAL    PRIMARY KEY,
    name     VARCHAR(255) NOT NULL,
    city     VARCHAR(100) NOT NULL,
    state    VARCHAR(2)   NOT NULL,
    logo_url VARCHAR(500)
);


CREATE TABLE rooms (
    id        BIGSERIAL    PRIMARY KEY,
    name      VARCHAR(255) NOT NULL,
    active    BOOLEAN      NOT NULL DEFAULT TRUE,
    cinema_id BIGINT       NOT NULL REFERENCES cinema(id)
);

CREATE UNIQUE INDEX idx_room_name_cinema ON rooms(name, cinema_id);

CREATE TABLE seats (
    id            BIGSERIAL   PRIMARY KEY,
    room_id       BIGINT      NOT NULL REFERENCES rooms(id),
    row_letter    VARCHAR(1)  NOT NULL,
    column_number INTEGER     NOT NULL,
    code          VARCHAR(10) NOT NULL,
    type          VARCHAR(50) NOT NULL,
    active        BOOLEAN     NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_seat_room UNIQUE (room_id, row_letter, column_number)
);

CREATE INDEX idx_seat_room ON seats(room_id);


CREATE TABLE movie_exhibition (
    id        BIGSERIAL    PRIMARY KEY,
    movie_id  BIGINT       NOT NULL REFERENCES movie(id),
    cinema_id BIGINT       NOT NULL REFERENCES cinema(id),
    format    VARCHAR(50)  NOT NULL,
    audio     VARCHAR(50)  NOT NULL,
    active    BOOLEAN      NOT NULL DEFAULT TRUE,

    CONSTRAINT uq_movie_exhibition UNIQUE (movie_id, cinema_id, format, audio)
);

CREATE INDEX idx_movie_exhibition_movie_id ON movie_exhibition(movie_id);
CREATE INDEX idx_movie_exhibition_cinema_id ON movie_exhibition(cinema_id);


CREATE TABLE movie_session (
    id            BIGSERIAL      PRIMARY KEY,
    show_date     DATE           NOT NULL,
    start_time    TIME           NOT NULL,
    end_time      TIME           NOT NULL,
    base_price    NUMERIC(10, 2) NOT NULL CHECK (base_price >= 0),
    canceled      BOOLEAN        NOT NULL DEFAULT FALSE,
    room_id       BIGINT         NOT NULL REFERENCES rooms(id),
    exhibition_id BIGINT         NOT NULL REFERENCES movie_exhibition(id),
    available_seats INTEGER      NOT NULL DEFAULT 0,

    CONSTRAINT chk_session_time CHECK (start_time < end_time)
);

CREATE INDEX idx_movie_session_exhibition_id ON movie_session(exhibition_id);
CREATE INDEX idx_movie_session_room_id ON movie_session(room_id);
CREATE INDEX idx_movie_session_show_date ON movie_session(show_date);
CREATE INDEX idx_movie_session_available ON movie_session(show_date, canceled);


CREATE TABLE users (
    id               UUID         PRIMARY KEY,
    name             VARCHAR(255),
    cpf              VARCHAR(14)  UNIQUE,
    email            VARCHAR(255) NOT NULL UNIQUE,
    password         VARCHAR(255) NOT NULL,
    phone            VARCHAR(20)  NOT NULL,
    gender           VARCHAR(20),
    zip_code         VARCHAR(10),
    street           VARCHAR(255),
    number           VARCHAR(20),
    complement       VARCHAR(255),
    neighborhood     VARCHAR(100),
    city             VARCHAR(100),
    state            VARCHAR(50),
    email_confirmed  BOOLEAN      NOT NULL DEFAULT FALSE,
    active           BOOLEAN      NOT NULL DEFAULT TRUE,
    data_joined      DATE,
    birthdate        DATE,
    failed_attempt   INTEGER      NOT NULL DEFAULT 0,
    lock_time        TIMESTAMP,
    is_locked        BOOLEAN      NOT NULL DEFAULT FALSE,
    role             VARCHAR(50)  NOT NULL,
    cinema_id        BIGINT       REFERENCES cinema(id)
);

CREATE INDEX idx_users_is_locked ON users(is_locked);


CREATE TABLE registration_confirmations (
    id         UUID         PRIMARY KEY,
    user_id    UUID         NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    code       VARCHAR(6)   NOT NULL,
    expires_at TIMESTAMP    NOT NULL,
    used       BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_registration_user_id ON registration_confirmations(user_id);


CREATE TABLE orders (
    id                     BIGSERIAL      PRIMARY KEY,
    version                BIGINT         NOT NULL DEFAULT 0,
    created_at             TIMESTAMP      NOT NULL,
    total_price            NUMERIC(10, 2) NOT NULL DEFAULT 0,
    service_fee            NUMERIC(10, 2) NOT NULL DEFAULT 0,
    total_tickets_count    INTEGER        NOT NULL DEFAULT 0,
    reservation_expires_at TIMESTAMP,
    user_id                UUID           NOT NULL REFERENCES users(id),
    session_id             BIGINT         NOT NULL REFERENCES movie_session(id),
    status                 VARCHAR(50)    NOT NULL
);

CREATE INDEX idx_order_user_id ON orders(user_id);
CREATE INDEX idx_order_session_id ON orders(session_id);
CREATE INDEX idx_order_status ON orders(status);


CREATE TABLE order_payment (
    id              BIGSERIAL      PRIMARY KEY,
    idempotency_key VARCHAR(100)   NOT NULL UNIQUE,
    payment_date    TIMESTAMP,
    provider_payment_id VARCHAR(100),
    version         BIGINT         NOT NULL DEFAULT 0,
    payment_method  VARCHAR(50)    NOT NULL,
    payment_status  VARCHAR(50)    NOT NULL,
    status_detail   VARCHAR(255),
    order_id        BIGINT         NOT NULL REFERENCES orders(id)
);

CREATE INDEX idx_order_payment_provider_payment_id ON order_payment(provider_payment_id);
CREATE INDEX idx_order_payment_status ON order_payment(payment_status);

CREATE TABLE payment_transaction (
    id                  BIGSERIAL       PRIMARY KEY,
    order_payment_id    BIGINT          NOT NULL REFERENCES order_payment(id),
    status              VARCHAR(50)     NOT NULL,
    source              VARCHAR(100),
    details             VARCHAR(255),
    gateway_timestamp   TIMESTAMP,
    created_at          TIMESTAMP       NOT NULL
);

CREATE TABLE ticket_types (
    id                     BIGSERIAL      PRIMARY KEY,
    name                   VARCHAR(255)   NOT NULL UNIQUE,
    price                  NUMERIC(10, 2) NOT NULL,
    category               VARCHAR(50)    NOT NULL,
    description            VARCHAR(255),
    active                 BOOLEAN        NOT NULL DEFAULT TRUE,
    max_quantity_per_order INTEGER
);


CREATE TABLE promotions (
    id                  BIGSERIAL      PRIMARY KEY,
    name                VARCHAR(255)   NOT NULL,
    cinema_id           BIGINT         NOT NULL REFERENCES cinema(id),
    day_of_week         VARCHAR(20),
    target_session_id   BIGINT         REFERENCES movie_session(id),
    discount_percentage NUMERIC(5, 2),
    fixed_price         NUMERIC(10, 2),
    active              BOOLEAN        NOT NULL DEFAULT TRUE
);


CREATE TABLE order_items (
    id             BIGSERIAL      PRIMARY KEY,
    order_id       BIGINT         NOT NULL REFERENCES orders(id),
    ticket_type_id BIGINT         NOT NULL REFERENCES ticket_types(id),
    quantity       INTEGER        NOT NULL CHECK (quantity > 0),
    unit_price     NUMERIC(10, 2) NOT NULL CHECK (unit_price >= 0),
    subtotal       NUMERIC(10, 2) NOT NULL CHECK (subtotal >= 0)
);

CREATE INDEX idx_order_item_order_id ON order_items(order_id);
CREATE INDEX idx_order_item_ticket_type_id ON order_items(ticket_type_id);


CREATE TABLE seat_reservations (
    id           BIGSERIAL   PRIMARY KEY,
    order_id     BIGINT      NOT NULL REFERENCES orders(id),
    session_id   BIGINT      NOT NULL REFERENCES movie_session(id),
    seat_id      BIGINT      NOT NULL REFERENCES seats(id),
    status       VARCHAR(50) NOT NULL,
    expires_at   TIMESTAMP   NOT NULL,
    CONSTRAINT uq_seat_reservation UNIQUE (session_id, seat_id)
);

CREATE INDEX idx_seat_reservation_session_id ON seat_reservations(session_id);
CREATE INDEX idx_seat_reservation_order_id ON seat_reservations(order_id);
CREATE INDEX idx_seat_reservation_active ON seat_reservations(session_id, seat_id, expires_at);

CREATE TABLE coupons (
    id              BIGSERIAL      PRIMARY KEY,
    code            VARCHAR(255)   NOT NULL UNIQUE,
    description     VARCHAR(255)   NOT NULL,
    discount_type   VARCHAR(50)    NOT NULL,
    discount_value  NUMERIC(10, 2) NOT NULL,
    expiration_date TIMESTAMP,
    user_id         UUID           REFERENCES users(id),
    cinema_id       BIGINT         REFERENCES cinema(id),
    status          VARCHAR(50)    NOT NULL DEFAULT 'AVAILABLE',
    active          BOOLEAN        NOT NULL DEFAULT TRUE
);

CREATE INDEX idx_coupon_user_id ON coupons(user_id);
CREATE INDEX idx_coupon_code ON coupons(code);
CREATE INDEX idx_coupon_status ON coupons(status);
CREATE INDEX idx_coupon_cinema_id ON coupons(cinema_id);
