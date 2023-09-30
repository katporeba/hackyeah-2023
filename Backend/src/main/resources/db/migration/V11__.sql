CREATE SEQUENCE IF NOT EXISTS category_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE category
(
    id          BIGINT NOT NULL,
    name        VARCHAR(255),
    description VARCHAR(500),
    CONSTRAINT pk_category PRIMARY KEY (id)
);
