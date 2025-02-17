package com.yerzhan.tennis.table_tennis.controller;

import com.yerzhan.tennis.table_tennis.entity.Games;
import com.yerzhan.tennis.table_tennis.entity.Users;
import com.yerzhan.tennis.table_tennis.repository.UserRepository;
import com.yerzhan.tennis.table_tennis.service.GameService;
import com.yerzhan.tennis.table_tennis.service.impl.UserDetailServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/games")
@RequiredArgsConstructor
public class GameController {

    private final GameService gamesService;
    private final UserRepository userRepository;
    private final UserDetailServiceImpl userDetailService;


    @GetMapping("/add-game")
    public String showAddGameForm(Model model, Principal principal) {
        Users currentUser = userDetailService.findByUsername(principal.getName());

        List<Users> availablePlayers = userDetailService.getAllUsersByName(currentUser.getUsername());
        model.addAttribute("currentUser",currentUser);
        model.addAttribute("availablePlayers",availablePlayers);
        model.addAttribute("game", new Games());
        return "addGame";
    }

    @PostMapping("/add-game")
    public String createGame(@RequestParam String player2Name,
                             Principal principal) {


        Users currentUser = userDetailService.findByUsername(principal.getName());
        Users opponent = userDetailService.findByUsername(player2Name);
        if (opponent == null) {
            throw new IllegalStateException("Один из игроков не найден");
        }
        // Создание игры
        Games games = new Games();
        games.setPlayer1(currentUser);
        games.setPlayer2(opponent);

        gamesService.createGame(games);
        return "redirect:/profile";
    }

    @GetMapping("/all")
    public String getAvailablePlayers(Principal principal, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Current user: ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;" + auth.getName());

        if (principal == null) {
            throw new RuntimeException("Пользователь не аутентифицирован");
        }

        String currentUsername = principal.getName();
        List<String> players = userRepository.findAll()
                .stream()
                .filter(user -> !user.getUsername().equals(currentUsername))
                .map(Users::getUsername)
                .collect(Collectors.toList());

        model.addAttribute("players", players);
        return "players";
    }



    @GetMapping("/invites")
    public String showInvites(@AuthenticationPrincipal UserDetails userDetails,Model model){
        Users user = userDetailService.findByUsername(userDetails.getUsername());
        List<Games> invites = gamesService.getPendingGamesForUser(user);
        model.addAttribute("invites",invites);
        return "invites";
    }
    @PostMapping("/invites/accept")
    public String acceptInvite(@RequestParam int gameId) {
        gamesService.acceptInvite(gameId);
        return "redirect:/games";
    }

//    @GetMapping
//    public String getAvailablePlayers(@AuthenticationPrincipal Users currentUser, Model model) {
//        List<String> players = userRepository.findAll()
//                .stream()
//                .filter(user -> !user.getUsername().equals(currentUser.getUsername())) // Исключаем себя
//                .map(Users::getUsername) // Берём только имя
//                .collect(Collectors.toList());
//
//        model.addAttribute("players", players); // Передаём список в шаблон
//        return "players"; // Название HTML-шаблона (players.html)
    }




//@GetMapping("/add-game")
//public String showAddGameForm(Model model) {
//    List<Users> players = userDetailService.getAllUsers();
//    model.addAttribute("players",players);
//    model.addAttribute("game",new Games());
//    return "addGame";
//}

//@PostMapping("/add-game")
//public String createGame(@RequestParam String player1Name,
//                         @RequestParam String player2Name,
//                         @RequestParam (required = false) Integer playerScore2) {
//
//    Users player1 = userRepository.findByUsername(player1Name);
//    Users player2 = userRepository.findByUsername(player2Name);
//
//    if (player1 == null || player2 == null) {
//        throw new IllegalStateException("Один из игроков не найден");
//    }
//    // Создание игры
//    Games games = new Games();
//    games.setPlayer2(player2);
//    games.setPlayerScore2(playerScore2);
//    gamesService.createGame(games);
//    return "redirect:/games/all";
//}

//@GetMapping
//public String getGamesPage(Model model){
//    Users users = userDetailService.getAuthenticatedUsers();
//    List<Games>  games = userDetailService.getGamesForUsers(users);
//    model.addAttribute("games",games);
//    return "games";
//}





//@GetMapping("/all")
//public String getAllGames(Model model) {
//    List<Games> games = gamesService.getAllGames();
//    model.addAttribute("games", games);
//    return "game";
//}