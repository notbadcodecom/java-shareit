CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(63) NOT NULL,
    email VARCHAR(511) NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS item_requests (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    requester_id BIGINT NOT NULL,
    description VARCHAR(511) NOT NULL,
    created TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_request PRIMARY KEY (id),
    CONSTRAINT fk_requester FOREIGN KEY (requester_id)
        REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS items (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(63) NOT NULL,
    description VARCHAR(511) NOT NULL,
    available BOOLEAN NOT NULL,
    owner_id BIGINT NOT NULL,
    request_id BIGINT DEFAULT NULL,
    CONSTRAINT pk_item PRIMARY KEY (id),
    CONSTRAINT fk_owner FOREIGN KEY (owner_id)
        REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_request FOREIGN KEY (request_id)
        REFERENCES item_requests (id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS comments (
    id BIGINT GENERATED  BY DEFAULT  AS IDENTITY  NOT NULL,
    author_id BIGINT NOT NULL,
    text VARCHAR(511) NOT NULL,
    item_id BIGINT NOT NULL,
    created TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_comments PRIMARY KEY (id),
    CONSTRAINT fk_author FOREIGN KEY (author_id)
        REFERENCES users (id) ON DELETE SET NULL,
    CONSTRAINT fk_item_comment FOREIGN KEY (item_id)
        REFERENCES items (id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    start_at TIMESTAMP NOT NULL,
    end_at TIMESTAMP NOT NULL,
    item_id BIGINT NOT NULL,
    booker_id BIGINT NOT NULL,
    status VARCHAR(511) NOT NULL,
    created TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_booking PRIMARY KEY (id),
    CONSTRAINT fk_item_booking FOREIGN KEY (item_id)
        REFERENCES items (id) ON DELETE CASCADE,
    CONSTRAINT fk_booker FOREIGN KEY (booker_id)
        REFERENCES users (id) ON DELETE CASCADE
);
