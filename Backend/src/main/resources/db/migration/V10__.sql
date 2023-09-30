ALTER TABLE tickets
DROP
COLUMN latitude;

ALTER TABLE tickets
DROP
COLUMN longitude;

ALTER TABLE tickets
    ADD latitude DOUBLE PRECISION;

ALTER TABLE tickets
    ADD longitude DOUBLE PRECISION;