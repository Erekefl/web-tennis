package com.yerzhan.tennis.table_tennis.dto;

import com.yerzhan.tennis.table_tennis.utils.GameStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GameDTO {
    private Integer id;

    @NotNull(message = "Игрок не может быть пустым")
    private UserDTO player;

    @NotNull(message = "Противник не может быть пустым")
    private UserDTO opponent;

    @Min(value = 0, message = "Счет игрока не может быть отрицательным")
    private int playerScore;

    @Min(value = 0, message = "Счет противника не может быть отрицательным")
    private int opponentScore;

    @NotNull(message = "Статус игры не может быть пустым")
    private GameStatus gameStatus;

    private LocalDateTime gameDate;
} 