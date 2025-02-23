package com.yerzhan.tennis.table_tennis.repository;

import com.yerzhan.tennis.table_tennis.entity.ChatMessage;
import com.yerzhan.tennis.table_tennis.entity.Games;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {
    List<ChatMessage> findByGameIdOrderBySentAtAsc(Integer gameId);

    boolean existsByGameAndRecipientUsernameAndIsReadFalse(Games game, String username);

    List<ChatMessage> findByGameAndRecipientUsernameAndIsReadFalse(Games game, String username);
} 