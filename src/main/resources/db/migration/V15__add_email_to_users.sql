ALTER TABLE users
    ADD COLUMN IF NOT EXISTS email VARCHAR(255) UNIQUE,
    ADD COLUMN IF NOT EXISTS enabled BOOLEAN NOT NULL DEFAULT false;

-- Обновляем существующие записи, устанавливая email на основе username
UPDATE users SET email = username || '@example.com' WHERE email IS NULL; 