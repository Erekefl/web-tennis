CREATE TABLE IF NOT EXISTS slider_images (
    id SERIAL PRIMARY KEY,
    image_url VARCHAR(500) NOT NULL,
    display_order INTEGER,
    is_active BOOLEAN NOT NULL DEFAULT true
); 