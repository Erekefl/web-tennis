-- Удаляем старые колонки
ALTER TABLE theme_settings 
    DROP COLUMN IF EXISTS background_color,
    DROP COLUMN IF EXISTS is_using_image; 