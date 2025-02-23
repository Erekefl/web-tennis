CREATE TABLE IF NOT EXISTS game_rounds (
    id SERIAL PRIMARY KEY,
    game_id INTEGER NOT NULL REFERENCES games(id),
    player_score INTEGER NOT NULL,
    opponent_score INTEGER NOT NULL,
    round_number INTEGER NOT NULL,
    round_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_game FOREIGN KEY (game_id) REFERENCES games(id)
); 