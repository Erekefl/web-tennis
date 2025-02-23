package com.yerzhan.tennis.table_tennis.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "game_rounds")
@AllArgsConstructor
@NoArgsConstructor
public class GameRound {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Games game;

    @Column(nullable = false)
    private int playerScore;

    @Column(nullable = false)
    private int opponentScore;

    @Column(name = "round_number", nullable = false)
    private int roundNumber;

    @Column(name = "round_date")
    private LocalDateTime roundDate;

    @PrePersist
    public void prePersist() {
        if (this.roundDate == null) {
            this.roundDate = LocalDateTime.now();
        }
    }
} 