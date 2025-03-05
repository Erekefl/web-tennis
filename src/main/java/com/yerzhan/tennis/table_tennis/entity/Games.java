package com.yerzhan.tennis.table_tennis.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yerzhan.tennis.table_tennis.utils.GameStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "games")
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Games {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "player_id")
    @JsonIgnoreProperties({"password", "authorities", "role", "games", "accountNonExpired", "accountNonLocked", "credentialsNonExpired", "enabled"})
    private Users player;

    @ManyToOne
    @JoinColumn(name = "opponent_id")
    @JsonIgnoreProperties({"password", "authorities", "role", "games", "accountNonExpired", "accountNonLocked", "credentialsNonExpired", "enabled"})
    private Users opponent;

    @Column(nullable = false)
    private int playerScore = 0;

    @Column(nullable = false)
    private int opponentScore = 0;

    @Column(name = "player_points", nullable = false)
    private int playerPoints = 0;

    @Column(name = "opponent_points", nullable = false)
    private int opponentPoints = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "game_status", nullable = false)
    private GameStatus gameStatus;

    @Column(name = "game_date")
    private LocalDateTime gameDate;

    @Transient
    private boolean hasUnreadMessages;

    @PrePersist
    public void prePersist() {
        if (this.gameDate == null) {
            this.gameDate = LocalDateTime.now();
        }
    }

    public boolean hasUnreadMessages() {
        return hasUnreadMessages;
    }

    public void setHasUnreadMessages(boolean hasUnreadMessages) {
        this.hasUnreadMessages = hasUnreadMessages;
    }
}
