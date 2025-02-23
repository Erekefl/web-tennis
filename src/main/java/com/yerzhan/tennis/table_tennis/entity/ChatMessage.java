package com.yerzhan.tennis.table_tennis.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "chat_messages")
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "game_id")
    @JsonIgnoreProperties({"player", "opponent", "gameStatus", "playerScore", "opponentScore", "gameDate"})
    private Games game;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    @JsonIgnoreProperties({"password", "authorities", "role", "games", "accountNonExpired", "accountNonLocked", "credentialsNonExpired", "enabled"})
    private Users sender;

    @ManyToOne
    @JoinColumn(name = "recipient_id")
    @JsonIgnoreProperties({"password", "authorities", "role", "games", "accountNonExpired", "accountNonLocked", "credentialsNonExpired", "enabled"})
    private Users recipient;

    @Column(nullable = false)
    private String content;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "is_read")
    private boolean isRead = false;

    @PrePersist
    public void prePersist() {
        if (this.sentAt == null) {
            this.sentAt = LocalDateTime.now();
        }
    }
} 