-- Сначала добавляем колонку как NULL
ALTER TABLE chat_messages
    ADD COLUMN recipient_id INTEGER REFERENCES users(id);

-- Обновляем существующие записи: если отправитель - игрок, то получатель - оппонент, и наоборот
UPDATE chat_messages cm
SET recipient_id = (
    CASE 
        WHEN cm.sender_id = (SELECT player_id FROM games WHERE id = cm.game_id)
        THEN (SELECT opponent_id FROM games WHERE id = cm.game_id)
        ELSE (SELECT player_id FROM games WHERE id = cm.game_id)
    END
);

-- Теперь делаем колонку NOT NULL
ALTER TABLE chat_messages
    ALTER COLUMN recipient_id SET NOT NULL; 