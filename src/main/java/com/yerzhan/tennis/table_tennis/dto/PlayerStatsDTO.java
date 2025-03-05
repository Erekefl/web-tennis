package com.yerzhan.tennis.table_tennis.dto;

import lombok.Data;

@Data
public class PlayerStatsDTO {
    private String username;
    private int totalGames;
    private int wins;
    private int losses;
    private double winRate;

    public void calculateWinRate() {
        if (totalGames > 0) {
            winRate = Math.round((double) wins / totalGames * 100);
        } else {
            winRate = 0;
        }
    }
} 