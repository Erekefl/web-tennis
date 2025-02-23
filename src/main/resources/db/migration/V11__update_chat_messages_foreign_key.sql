-- Сначала удаляем существующее ограничение
ALTER TABLE chat_messages
    DROP CONSTRAINT IF EXISTS chat_messages_game_id_fkey;

-- Добавляем новое ограничение с каскадным удалением
ALTER TABLE chat_messages
    ADD CONSTRAINT chat_messages_game_id_fkey
    FOREIGN KEY (game_id)
    REFERENCES games(id)
    ON DELETE CASCADE; 