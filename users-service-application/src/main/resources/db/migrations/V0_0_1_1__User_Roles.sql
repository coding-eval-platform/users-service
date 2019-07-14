CREATE TABLE user_roles
(
    user_id BIGSERIAL PRIMARY KEY NOT NULL,
    role    VARCHAR               NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id)
);
