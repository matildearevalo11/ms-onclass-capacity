CREATE TABLE IF NOT EXISTS capacities (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name LONGTEXT NOT NULL,
    description LONGTEXT NOT NULL,
    CONSTRAINT ck_capacity_name CHECK (CHAR_LENGTH(TRIM(name)) > 0),
    CONSTRAINT ck_capacity_description CHECK (CHAR_LENGTH(TRIM(description)) > 0)
);
