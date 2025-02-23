package com.yerzhan.tennis.table_tennis.repository;

import com.yerzhan.tennis.table_tennis.entity.Games;
import com.yerzhan.tennis.table_tennis.entity.Users;
import com.yerzhan.tennis.table_tennis.utils.GameStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GamesRepository extends JpaRepository<Games, Integer> {
    List<Games> findByPlayer_Username(String username);
    List<Games> findByOpponent_Username(String username);
    List<Games> findByOpponent_UsernameAndGameStatus(String username, GameStatus status);
    List<Games> findByPlayer_UsernameAndGameStatus(String username, GameStatus status);
    Optional<Games> findByPlayerAndOpponentAndGameStatus(Users player, Users opponent, GameStatus status);
    List<Games> findByOpponentUsernameAndGameStatusIn(String username, List<GameStatus> statuses);
    List<Games> findByPlayerUsernameAndGameStatusIn(String username, List<GameStatus> statuses);
}
