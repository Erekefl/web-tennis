package com.yerzhan.tennis.table_tennis.service;

import com.yerzhan.tennis.table_tennis.entity.ChatMessage;
import com.yerzhan.tennis.table_tennis.entity.Games;
import com.yerzhan.tennis.table_tennis.entity.Users;
import com.yerzhan.tennis.table_tennis.repository.ChatMessageRepository;
import com.yerzhan.tennis.table_tennis.repository.GamesRepository;
import com.yerzhan.tennis.table_tennis.repository.UserRepository;
import com.yerzhan.tennis.table_tennis.utils.GameStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {
    private final ChatMessageRepository chatMessageRepository;
    private final GamesRepository gamesRepository;
    private final UserRepository userRepository;

    @Transactional
    public ChatMessage saveMessage(Integer gameId, String username, String content) {
        log.info("Попытка сохранения сообщения. GameId: {}, Username: {}", gameId, username);
        
        // Проверяем существование игры
        Games game = gamesRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Игра не найдена"));

        // Проверяем статус игры
        if (game.getGameStatus() != GameStatus.ACCEPTED) {
            log.error("Попытка отправки сообщения в игру с неверным статусом: {}", game.getGameStatus());
            throw new RuntimeException("Чат доступен только для принятых игр");
        }

        // Проверяем существование отправителя
        Users sender = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Отправитель не найден"));

        // Определяем получателя (если отправитель - игрок, то получатель - оппонент и наоборот)
        Users recipient = username.equals(game.getPlayer().getUsername()) ? 
                         game.getOpponent() : game.getPlayer();

        // Проверяем содержимое сообщения
        if (content == null || content.trim().isEmpty()) {
            throw new RuntimeException("Сообщение не может быть пустым");
        }

        try {
            ChatMessage message = new ChatMessage();
            message.setGame(game);
            message.setSender(sender);
            message.setRecipient(recipient);
            message.setContent(content.trim());
            message.setRead(false);

            ChatMessage savedMessage = chatMessageRepository.save(message);
            log.info("Сообщение успешно сохранено с ID: {}", savedMessage.getId());
            return savedMessage;
        } catch (Exception e) {
            log.error("Ошибка при сохранении сообщения: {}", e.getMessage());
            throw new RuntimeException("Ошибка при сохранении сообщения");
        }
    }

    public List<ChatMessage> getGameMessages(Integer gameId) {
        log.info("Загрузка сообщений для игры {}", gameId);
        List<ChatMessage> messages = chatMessageRepository.findByGameIdOrderBySentAtAsc(gameId);
        log.info("Найдено {} сообщений для игры {}", messages.size(), gameId);
        return messages;
    }

    @Transactional
    public void markMessagesAsRead(Integer gameId, String username) {
        Games game = gamesRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Игра не найдена"));

        List<ChatMessage> unreadMessages = chatMessageRepository.findByGameAndRecipientUsernameAndIsReadFalse(game, username);
        unreadMessages.forEach(message -> message.setRead(true));
        chatMessageRepository.saveAll(unreadMessages);
    }
} 