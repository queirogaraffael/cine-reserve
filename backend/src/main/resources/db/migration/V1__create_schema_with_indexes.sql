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
    id               UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    name             VARCHAR(255),
    cpf              VARCHAR(14)  UNIQUE,
    email            VARCHAR(255) NOT NULL UNIQUE,
    password         VARCHAR(255) NOT NULL,
    celular          VARCHAR(20)  NOT NULL,
    sexo             VARCHAR(20),
    cep              VARCHAR(10),
    logradouro       VARCHAR(255),
    numero           VARCHAR(20),
    complemento      VARCHAR(255),
    bairro           VARCHAR(100),
    cidade           VARCHAR(100),
    estado           VARCHAR(50),
    email_confirmado BOOLEAN      NOT NULL DEFAULT FALSE,
    ativo            BOOLEAN      NOT NULL DEFAULT TRUE,
    data_joined      DATE,
    birthdate        DATE,
    failed_attempt   INTEGER      NOT NULL DEFAULT 0,
    lock_time        TIMESTAMP,
    is_locked        BOOLEAN      NOT NULL DEFAULT FALSE,
    role             VARCHAR(50)  NOT NULL
);

CREATE INDEX idx_users_is_locked ON users(is_locked) WHERE is_locked = TRUE;

CREATE INDEX idx_users_ativo ON users(ativo) WHERE ativo = FALSE;

CREATE TABLE confirmacoes_cadastro (
    id             UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id     UUID         NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    codigo         VARCHAR(6)   NOT NULL,
    data_expiracao TIMESTAMP    NOT NULL,
    utilizado      BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_confirmacao_usuario_id ON confirmacoes_cadastro(usuario_id);


CREATE TABLE purchase (
                          id               BIGSERIAL      PRIMARY KEY,
                          purchase_date    TIMESTAMP      NOT NULL,
                          total_price      NUMERIC(10, 2) NOT NULL DEFAULT 0,
                          idempotency_key  CHAR(36)       NOT NULL UNIQUE,
                          purchase_status  VARCHAR(50)    NOT NULL,
                          version          BIGINT         NOT NULL DEFAULT 0,
                          user_id          UUID           NOT NULL REFERENCES users(id)
);

CREATE INDEX idx_purchase_user_id ON purchase(user_id);
CREATE INDEX idx_purchase_status ON purchase(purchase_status);
CREATE INDEX idx_purchase_user_status ON purchase(user_id, purchase_status);

CREATE TABLE payment (
                         id              BIGSERIAL      PRIMARY KEY,
                         payment_date    TIMESTAMP,
                         transaction_id  BIGINT,
                         version         BIGINT         NOT NULL DEFAULT 0,
                         payment_method  VARCHAR(50)    NOT NULL,
                         payment_status  VARCHAR(50)    NOT NULL,
                         status_detail   VARCHAR(255),
                         purchase_id     BIGINT         NOT NULL UNIQUE REFERENCES purchase(id)
);
CREATE INDEX idx_payment_transaction_id ON payment(transaction_id);

CREATE INDEX idx_payment_status ON payment(payment_status);

CREATE TABLE tickets (
    id           BIGSERIAL      PRIMARY KEY,
    seat_number  INTEGER        NOT NULL,
    category     VARCHAR(50)    NOT NULL,
    price        NUMERIC(10, 2) NOT NULL CHECK (price > 0),
    session_id   BIGINT         NOT NULL REFERENCES movie_session(id),
    purchase_id  BIGINT         NOT NULL REFERENCES purchase(id),

    CONSTRAINT uq_ticket_session_seat UNIQUE (session_id, seat_number)
);

CREATE INDEX idx_ticket_purchase_id ON tickets(purchase_id);

CREATE INDEX idx_ticket_session_id ON tickets(session_id);

CREATE TABLE seat_reservations (
    id           BIGSERIAL   PRIMARY KEY,
    seat_number  INTEGER     NOT NULL,
    status       VARCHAR(50) NOT NULL,
    expires_at   TIMESTAMP   NOT NULL,
    session_id   BIGINT      NOT NULL REFERENCES movie_session(id),
    user_id      UUID        NOT NULL REFERENCES users(id)
);

CREATE INDEX idx_seat_reservation_session_id ON seat_reservations(session_id);

CREATE INDEX idx_seat_reservation_user_id ON seat_reservations(user_id);

CREATE INDEX idx_seat_reservation_active ON seat_reservations(session_id, seat_number, expires_at)
    WHERE status = 'RESERVED';
