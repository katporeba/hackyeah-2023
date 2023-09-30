ALTER TABLE tickets
    ADD latitude BIGINT;

ALTER TABLE tickets
    ADD longitude BIGINT;

ALTER TABLE tickets
DROP
CONSTRAINT fk_tickets_on_position;

ALTER TABLE tickets
DROP
COLUMN position_id;