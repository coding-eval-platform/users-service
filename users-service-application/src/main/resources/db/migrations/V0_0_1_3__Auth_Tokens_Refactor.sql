CREATE TABLE user_auth_tokens
(
    token_id UUID,
    user_id  BIGINT,
    FOREIGN KEY (token_id) REFERENCES auth_tokens (id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE SET NULL ON UPDATE SET NULL
);

CREATE TABLE subject_auth_tokens
(
    token_id UUID,
    subject  VARCHAR,
    FOREIGN KEY (token_id) REFERENCES auth_tokens (id) ON DELETE CASCADE ON UPDATE CASCADE
);
