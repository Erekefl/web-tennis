package com.yerzhan.tennis.table_tennis.service;

import com.yerzhan.tennis.table_tennis.entity.Games;
import com.yerzhan.tennis.table_tennis.entity.Users;

import java.util.List;

public interface GameService {

    Games createGame(Games game);
    List<Games> getPendingGamesForUser(Users username);
    public void acceptInvite(int gameId);


    List<Games> getAllGames();

    public Games getGamesById(int id);

    public void updateGames(int id,Games updateGames);


}
