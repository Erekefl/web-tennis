CREATE TABLE IF NOT EXISTS theme_settings (
    id SERIAL PRIMARY KEY,
    background_color VARCHAR(50) NOT NULL DEFAULT '#f8f9fa',
    background_image_url VARCHAR(500),
    is_using_image BOOLEAN NOT NULL DEFAULT false,
    version BIGINT NOT NULL DEFAULT 0
); 