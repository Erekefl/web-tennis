CREATE TABLE IF NOT EXISTS games (
    id SERIAL PRIMARY KEY,
    player_id INTEGER NOT NULL REFERENCES users(id),
    opponent_id INTEGER NOT NULL REFERENCES users(id),
    player_score INTEGER NOT NULL DEFAULT 0,
    game_status VARCHAR(50) NOT NULL,
    game_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_players_status UNIQUE (player_id, opponent_id, game_status)
); 