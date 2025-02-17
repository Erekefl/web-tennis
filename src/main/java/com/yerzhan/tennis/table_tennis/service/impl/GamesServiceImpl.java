package com.yerzhan.tennis.table_tennis.service.impl;

import com.yerzhan.tennis.table_tennis.entity.Games;
import com.yerzhan.tennis.table_tennis.entity.Users;
import com.yerzhan.tennis.table_tennis.repository.GamesRepository;
import com.yerzhan.tennis.table_tennis.repository.UserRepository;
import com.yerzhan.tennis.table_tennis.service.GameService;
import com.yerzhan.tennis.table_tennis.utils.GameStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@Service
@RequiredArgsConstructor
public class GamesServiceImpl implements GameService {


    private final GamesRepository gamesRepository;
    private final UserRepository userRepository;

    @Override
    public Games createGame(Games game) {
        return gamesRepository.save(game);
    }

    @Override
    public List<Games> getPendingGamesForUser(Users username) {
        return List.of();
    }

//    @Override
//    public List<Games> getPendingGamesForUser(String username) {
//        return List.of();
//    }

    @Override
    public List<Games> getAllGames() {
        return gamesRepository.findAll();
    }

    @Override
    public Games getGamesById(int id) {
        return gamesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Игра не найдена"));
    }

    public void updateGames(int id,Games updateGames){
        Games existingGames = getGamesById(id);
        existingGames.setPlayerScore1(updateGames.getPlayerScore1());
        existingGames.setPlayerScore2(updateGames.getPlayerScore2());
        existingGames.setGameDate(updateGames.getGameDate());

        gamesRepository.save(existingGames);
    }

    public Games createGamesInvite(String player1username,String player2username){
        Users player1 = userRepository.findByUsername(player1username);
        if (player1 == null){
            throw new RuntimeException("Игрок 1 не найден");
        }


        Users player2 = userRepository.findByUsername(player2username);
        if (player2 == null) {
            throw new RuntimeException("Игрок 2 не найден");
        }

        Games game = new Games();
        game.setPlayer1(player1);
        game.setPlayer2(player2);
        game.setGameStatus(GameStatus.PENDING);
        game.setGameDate(LocalDateTime.now());

        return gamesRepository.save(game);
    }


    public void acceptInvite(int gameId){
        Games games = gamesRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Игра не найдена"));

        games.setGameStatus(GameStatus.ACTIVE);
        gamesRepository.save(games);

    }

//    public List<Games> getPendingGamesForUser(String username){
//        Users player = userRepository.findByUsername(username);
//              if (player == null){
//                  throw  new RuntimeException("User not found");
//              }
//        return gamesRepository.findByPlayer2AndGameStatus(player,GameStatus.PENDING);
//    }


}
