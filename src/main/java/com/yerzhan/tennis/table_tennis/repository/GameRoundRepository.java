package com.yerzhan.tennis.table_tennis.repository;

import com.yerzhan.tennis.table_tennis.entity.GameRound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GameRoundRepository extends JpaRepository<GameRound, Integer> {
    List<GameRound> findByGameIdOrderByRoundDateDesc(Integer gameId);
    
    @Modifying
    @Query("DELETE FROM GameRound gr WHERE gr.game.id = :gameId")
    void deleteByGameId(Integer gameId);
} 