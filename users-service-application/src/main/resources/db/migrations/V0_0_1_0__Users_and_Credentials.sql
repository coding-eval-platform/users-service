CREATE TABLE users
(
    id       BIGSERIAL PRIMARY KEY NOT NULL,
    username VARCHAR(64)           NOT NULL,
    active   BOOLEAN               NOT NULL DEFAULT TRUE
);

CREATE UNIQUE INDEX users_username_unique_index
    ON users (username);

CREATE TABLE user_credentials
(
    id              BIGSERIAL PRIMARY KEY NOT NULL,
    user_id         BIGINT                NOT NULL,
    hashed_password VARCHAR               NOT NULL,
    created_at      TIMESTAMP             NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);
