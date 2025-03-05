CREATE TABLE IF NOT EXISTS confirmation_tokens (
    id SERIAL PRIMARY KEY,
    token VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    confirmed_at TIMESTAMP,
    user_id INTEGER NOT NULL REFERENCES users(id),
    token_type VARCHAR(50) NOT NULL,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
); 