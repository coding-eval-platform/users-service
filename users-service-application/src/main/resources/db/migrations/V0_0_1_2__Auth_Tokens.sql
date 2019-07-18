CREATE TABLE auth_tokens
(
    id         UUID PRIMARY KEY NOT NULL,
    user_id    BIGINT,
    created_at TIMESTAMP,
    valid      BOOLEAN,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE SET NULL ON UPDATE SET NULL
);

CREATE TABLE token_roles
(
    token_id UUID,
    role     VARCHAR,
    FOREIGN KEY (token_id) REFERENCES auth_tokens (id) ON DELETE CASCADE ON UPDATE CASCADE
);
