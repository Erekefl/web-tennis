ALTER TABLE chat_messages
    ADD COLUMN is_read BOOLEAN NOT NULL DEFAULT false; 