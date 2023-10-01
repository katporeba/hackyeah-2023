CREATE TABLE IF NOT EXISTS tickets
(
    id               BIGINT  NOT NULL,
    ticket_id        VARCHAR(255),
    category         VARCHAR(255),
    count            INTEGER,
    aggressive       BOOLEAN NOT NULL,
    comment          VARCHAR(255),
    expiration_date  TIMESTAMP WITHOUT TIME ZONE,
    ticket_image_url VARCHAR(255),
    latitude         DOUBLE PRECISION,
    longitude        DOUBLE PRECISION,
    user_id          BIGINT,
    CONSTRAINT pk_tickets PRIMARY KEY (id)
);

ALTER TABLE tickets DROP CONSTRAINT IF EXISTS FK_TICKETS_ON_USER;
ALTER TABLE tickets ADD CONSTRAINT FK_TICKETS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);