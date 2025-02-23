-- Сначала удаляем существующее ограничение
ALTER TABLE games DROP CONSTRAINT IF EXISTS games_game_status_check;

-- Добавляем новое ограничение с обновленным списком статусов
ALTER TABLE games ADD CONSTRAINT games_game_status_check 
    CHECK (game_status IN ('PENDING', 'ACCEPTED', 'REJECTED', 'CANCELLED', 'FINISHED')); 