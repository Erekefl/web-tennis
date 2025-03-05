package com.yerzhan.tennis.table_tennis.service.impl;

import com.yerzhan.tennis.table_tennis.dto.PlayerStatsDTO;
import com.yerzhan.tennis.table_tennis.entity.Games;
import com.yerzhan.tennis.table_tennis.entity.GameRound;
import com.yerzhan.tennis.table_tennis.entity.Users;
import com.yerzhan.tennis.table_tennis.repository.GamesRepository;
import com.yerzhan.tennis.table_tennis.repository.UserRepository;
import com.yerzhan.tennis.table_tennis.repository.GameRoundRepository;
import com.yerzhan.tennis.table_tennis.repository.ChatMessageRepository;
import com.yerzhan.tennis.table_tennis.utils.GameStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GamesService {

    private final GamesRepository gamesRepository;
    private final UserRepository userRepository;
    private final GameRoundRepository gameRoundRepository;
    private final ChatMessageRepository chatMessageRepository;

    public Games createGame(String currentUsername, String opponentUsername) {
        // Проверка, что игрок не пытается отправить приглашение самому себе
        if (currentUsername.equals(opponentUsername)) {
            throw new RuntimeException("Нельзя отправить приглашение самому себе");
        }

        Users player = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Текущий пользователь не найден"));
        Users opponent = userRepository.findByUsername(opponentUsername)
                .orElseThrow(() -> new RuntimeException("Противник не найден"));

        // Проверяем, нет ли уже активного приглашения между этими игроками
        boolean hasActiveInvite = gamesRepository.findByPlayerAndOpponentAndGameStatus(
                player, opponent, GameStatus.PENDING).isPresent();

        if (hasActiveInvite) {
            throw new RuntimeException("Приглашение для этого игрока уже существует");
        }

        Games game = new Games();
        game.setPlayer(player);
        game.setOpponent(opponent);
        game.setGameStatus(GameStatus.PENDING);

        return gamesRepository.save(game);
    }

    private void checkUnreadMessages(Games game, String username) {
        boolean hasUnread = chatMessageRepository.existsByGameAndRecipientUsernameAndIsReadFalse(
            game, username);
        game.setHasUnreadMessages(hasUnread);
    }

    public List<Games> getInvitedPlayers(String username) {
        List<Games> games = gamesRepository.findByOpponentUsernameAndGameStatusIn(
            username, 
            Arrays.asList(GameStatus.PENDING, GameStatus.ACCEPTED)
        );
        games.forEach(game -> checkUnreadMessages(game, username));
        return games;
    }

    public List<Games> getSentInvites(String username) {
        List<Games> games = gamesRepository.findByPlayerUsernameAndGameStatusIn(
            username,
            Arrays.asList(GameStatus.PENDING, GameStatus.ACCEPTED)
        );
        games.forEach(game -> checkUnreadMessages(game, username));
        return games;
    }

    public Games acceptGame(Integer gameId, String username) {
        Games game = gamesRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Игра не найдена"));

        // Проверяем, что текущий пользователь является получателем приглашения
        if (!game.getOpponent().getUsername().equals(username)) {
            throw new RuntimeException("У вас нет прав для принятия этого приглашения");
        }

        // Проверяем, что игра находится в статусе PENDING
        if (game.getGameStatus() != GameStatus.PENDING) {
            throw new RuntimeException("Это приглашение уже не может быть принято");
        }

        game.setGameStatus(GameStatus.ACCEPTED);
        return gamesRepository.save(game);
    }

    public Games rejectGame(Integer gameId, String username) {
        Games game = gamesRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Игра не найдена"));

        // Проверяем, что текущий пользователь является получателем приглашения
        if (!game.getOpponent().getUsername().equals(username)) {
            throw new RuntimeException("У вас нет прав для отклонения этого приглашения");
        }

        // Проверяем, что игра находится в статусе PENDING
        if (game.getGameStatus() != GameStatus.PENDING) {
            throw new RuntimeException("Это приглашение уже не может быть отклонено");
        }

        game.setGameStatus(GameStatus.REJECTED);
        return gamesRepository.save(game);
    }

    public Games cancelGame(Integer gameId, String username) {
        Games game = gamesRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Игра не найдена"));

        // Проверяем, что текущий пользователь является отправителем приглашения
        if (!game.getPlayer().getUsername().equals(username)) {
            throw new RuntimeException("У вас нет прав для отмены этого приглашения");
        }

        // Проверяем, что игра находится в статусе PENDING
        if (game.getGameStatus() != GameStatus.PENDING) {
            throw new RuntimeException("Это приглашение уже не может быть отменено");
        }

        game.setGameStatus(GameStatus.CANCELLED);
        return gamesRepository.save(game);
    }

    public void finishGame(Integer gameId, String username, Integer playerScore, Integer opponentScore) {
        Games game = gamesRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Игра не найдена"));

        if (!username.equals(game.getPlayer().getUsername()) && !username.equals(game.getOpponent().getUsername())) {
            throw new RuntimeException("У вас нет прав для завершения этой игры");
        }

        // Проверяем, что счет не равный
        if (playerScore.equals(opponentScore)) {
            throw new RuntimeException("Счет не может быть равным. Один из игроков должен победить.");
        }

        // Определяем, кто выиграл раунд
        int playerPoint = 0;
        int opponentPoint = 0;
        if (playerScore > opponentScore) {
            playerPoint = 1;
        } else {
            opponentPoint = 1;
        }

        // Сохраняем результаты раунда
        GameRound round = new GameRound();
        round.setGame(game);
        round.setPlayerScore(playerScore);
        round.setOpponentScore(opponentScore);
        round.setRoundNumber(gameRoundRepository.findByGameIdOrderByRoundDateDesc(gameId).size() + 1);
        gameRoundRepository.save(round);

        // Обновляем общий счет и баллы
        game.setPlayerScore(game.getPlayerScore() + playerScore);
        game.setOpponentScore(game.getOpponentScore() + opponentScore);
        game.setPlayerPoints(game.getPlayerPoints() + playerPoint);
        game.setOpponentPoints(game.getOpponentPoints() + opponentPoint);

        // Если сыграно 5 раундов, завершаем игру
        if (gameRoundRepository.findByGameIdOrderByRoundDateDesc(gameId).size() >= 5) {
            game.setGameStatus(GameStatus.FINISHED);
        }

        gamesRepository.save(game);
    }

    public void finishGameAndExit(Integer gameId, String username) {
        Games game = gamesRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Игра не найдена"));

        // Проверяем, что текущий пользователь является отправителем приглашения
        if (!game.getPlayer().getUsername().equals(username)) {
            throw new RuntimeException("Только отправитель приглашения может завершить игру");
        }

        // Получаем все раунды игры
        List<GameRound> rounds = gameRoundRepository.findByGameIdOrderByRoundDateDesc(gameId);
        
        // Считаем общий счет из существующих раундов
        int totalPlayerScore = rounds.stream().mapToInt(GameRound::getPlayerScore).sum();
        int totalOpponentScore = rounds.stream().mapToInt(GameRound::getOpponentScore).sum();

        // Устанавливаем общий счет и статус FINISHED
        game.setPlayerScore(totalPlayerScore);
        game.setOpponentScore(totalOpponentScore);
        game.setGameStatus(GameStatus.FINISHED);
        gamesRepository.save(game);
    }

    public Games startGame(Integer gameId, String username) {
        Games game = gamesRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Игра не найдена"));

        // Проверяем, что текущий пользователь является отправителем приглашения
        if (!game.getPlayer().getUsername().equals(username)) {
            throw new RuntimeException("Только отправитель приглашения может начать игру");
        }

        // Проверяем, что игра находится в статусе ACCEPTED
        if (game.getGameStatus() != GameStatus.ACCEPTED) {
            throw new RuntimeException("Игра не может быть начата, так как приглашение не принято");
        }

        return game;
    }

    public Games getGameHistory(Integer gameId, String username) {
        Games game = gamesRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Игра не найдена"));

        // Проверяем, что текущий пользователь является участником игры
        if (!game.getPlayer().getUsername().equals(username) && !game.getOpponent().getUsername().equals(username)) {
            throw new RuntimeException("У вас нет доступа к этой игре");
        }

        // Проверяем, что игра завершена
        if (game.getGameStatus() != GameStatus.FINISHED) {
            throw new RuntimeException("Эта игра еще не завершена");
        }

        return game;
    }

    public List<GameRound> getGameRounds(Integer gameId) {
        // Получаем все раунды и сортируем по номеру раунда (по возрастанию)
        return gameRoundRepository.findByGameIdOrderByRoundDateDesc(gameId)
                .stream()
                .sorted((r1, r2) -> Integer.compare(r1.getRoundNumber(), r2.getRoundNumber()))
                .distinct()
                .collect(java.util.stream.Collectors.toList());
    }

    public List<Games> getAllFinishedGames(String username) {
        // Получаем все игры, где пользователь был игроком или противником
        List<Games> playerGames = gamesRepository.findByPlayer_UsernameAndGameStatus(username, GameStatus.FINISHED);
        List<Games> opponentGames = gamesRepository.findByOpponent_UsernameAndGameStatus(username, GameStatus.FINISHED);

        // Объединяем списки
        List<Games> allGames = new ArrayList<>();
        allGames.addAll(playerGames);
        allGames.addAll(opponentGames);

        // Сортируем по дате (сначала новые)
        allGames.sort((g1, g2) -> g2.getGameDate().compareTo(g1.getGameDate()));

        return allGames;
    }

    public Games getGameForChat(Integer gameId, String username) {
        Games game = gamesRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Игра не найдена"));

        // Проверяем, что текущий пользователь является участником игры
        if (!game.getPlayer().getUsername().equals(username) && !game.getOpponent().getUsername().equals(username)) {
            throw new RuntimeException("У вас нет доступа к этому чату");
        }

        // Проверяем, что игра находится в статусе ACCEPTED
        if (game.getGameStatus() != GameStatus.ACCEPTED) {
            throw new RuntimeException("Чат доступен только для принятых игр");
        }

        return game;
    }

    public List<PlayerStatsDTO> getPlayersStats() {
        List<Users> allUsers = userRepository.findAll();
        List<PlayerStatsDTO> playerStats = new ArrayList<>();

        for (Users user : allUsers) {
            PlayerStatsDTO stats = new PlayerStatsDTO();
            stats.setUsername(user.getUsername());

            // Получаем все завершенные игры пользователя
            List<Games> playerGames = gamesRepository.findByPlayer_UsernameAndGameStatus(user.getUsername(), GameStatus.FINISHED);
            List<Games> opponentGames = gamesRepository.findByOpponent_UsernameAndGameStatus(user.getUsername(), GameStatus.FINISHED);

            int wins = 0;
            int totalGames = playerGames.size() + opponentGames.size();

            // Подсчитываем победы в играх, где пользователь был игроком
            for (Games game : playerGames) {
                if (game.getPlayerPoints() > game.getOpponentPoints()) {
                    wins++;
                }
            }

            // Подсчитываем победы в играх, где пользователь был оппонентом
            for (Games game : opponentGames) {
                if (game.getOpponentPoints() > game.getPlayerPoints()) {
                    wins++;
                }
            }

            stats.setTotalGames(totalGames);
            stats.setWins(wins);
            stats.setLosses(totalGames - wins);
            stats.calculateWinRate();

            playerStats.add(stats);
        }

        // Сортируем по проценту побед (по убыванию)
        playerStats.sort((a, b) -> Double.compare(b.getWinRate(), a.getWinRate()));

        return playerStats;
    }

}
