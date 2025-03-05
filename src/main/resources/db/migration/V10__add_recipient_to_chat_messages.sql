-- Сначала добавляем колонку как NULL
ALTER TABLE chat_messages
    ADD COLUMN recipient_id INTEGER REFERENCES users(id);

-- Делаем колонку NOT NULL
ALTER TABLE chat_messages
    ALTER COLUMN recipient_id SET NOT NULL; 