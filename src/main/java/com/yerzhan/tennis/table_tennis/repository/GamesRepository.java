package com.yerzhan.tennis.table_tennis.repository;

import com.yerzhan.tennis.table_tennis.entity.Games;
import com.yerzhan.tennis.table_tennis.entity.Users;
import com.yerzhan.tennis.table_tennis.utils.GameStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface GamesRepository extends JpaRepository<Games,Integer> {
    List<Games> findByPlayer2(Users player2);
//    @Query("SELECT g FROM Game g WHERE g.player2.username = :username AND g.gameStatus = :gameStatus")
//    List<Games> findByPlayer2AndGameStatus(@Param("username") Users player2, @Param("gameStatus") GameStatus gameStatus);

}
