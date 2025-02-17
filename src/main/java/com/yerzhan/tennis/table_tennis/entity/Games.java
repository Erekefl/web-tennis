package com.yerzhan.tennis.table_tennis.entity;

import com.yerzhan.tennis.table_tennis.utils.GameStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "games")
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Games {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;


    @ManyToOne
    @JoinColumn(name = "player1_id", nullable = false)
    private Users player1;


    @ManyToOne
    @JoinColumn(name = "player2_id", nullable = false)
    private Users player2;


    @Column(nullable = false)
    private int playerScore1;

    @Column(nullable = false)
    private int playerScore2;

    @Enumerated(EnumType.STRING)
    @Column(name = "game_status", nullable = false)
    private GameStatus gameStatus;

    @Column(name = "game_date")
    private LocalDateTime gameDate;


    @PrePersist
    public void prePersist() {
        if (this.gameDate == null) {
            this.gameDate = LocalDateTime.now();
        }
    }
}
