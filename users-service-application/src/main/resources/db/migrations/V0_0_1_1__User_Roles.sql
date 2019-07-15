CREATE TABLE user_roles
(
    user_id BIGINT,
    role    VARCHAR,
    FOREIGN KEY (user_id) REFERENCES users (id)
);
