CREATE SEQUENCE IF NOT EXISTS position_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS tickets_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE position
(
    id        BIGINT NOT NULL,
    latitude  BIGINT,
    longitude BIGINT,
    CONSTRAINT pk_position PRIMARY KEY (id)
);

CREATE TABLE tickets
(
    id               BIGINT  NOT NULL,
    ticket_id        VARCHAR(255),
    category         VARCHAR(255),
    count            INTEGER,
    aggressive       BOOLEAN NOT NULL,
    position_id      BIGINT,
    comment          VARCHAR(255),
    expiration_date  TIMESTAMP WITHOUT TIME ZONE,
    ticket_image_url VARCHAR(255),
    user_id          BIGINT,
    CONSTRAINT pk_tickets PRIMARY KEY (id)
);

ALTER TABLE tickets
    ADD CONSTRAINT FK_TICKETS_ON_POSITION FOREIGN KEY (position_id) REFERENCES position (id);

ALTER TABLE tickets
    ADD CONSTRAINT FK_TICKETS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);