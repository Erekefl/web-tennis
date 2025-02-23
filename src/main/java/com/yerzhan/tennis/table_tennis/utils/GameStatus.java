package com.yerzhan.tennis.table_tennis.utils;

public enum GameStatus {
    PENDING,    // Ожидает подтверждения
    ACCEPTED,   // Приглашение принято
    REJECTED,   // Приглашение отклонено
    CANCELLED,  // Приглашение отменено
    FINISHED    // Игра завершена
}
