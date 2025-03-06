package com.yerzhan.tennis.table_tennis.controller;

import com.yerzhan.tennis.table_tennis.entity.Games;
import com.yerzhan.tennis.table_tennis.entity.GameRound;
import com.yerzhan.tennis.table_tennis.entity.ChatMessage;
import com.yerzhan.tennis.table_tennis.service.impl.GamesService;
import com.yerzhan.tennis.table_tennis.service.ThemeSettingsService;
import com.yerzhan.tennis.table_tennis.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/games")
@RequiredArgsConstructor    
public class GameController {

    private final GamesService gamesService;
    private final ThemeSettingsService themeSettingsService;
    private final ChatService chatService;

    @PostMapping("/create-and-accept")
    @ResponseBody
    public ResponseEntity<?> createAndAcceptGame(@RequestParam String opponentUsername, Authentication authentication) {
        try {
            Games newGame = gamesService.createGame(authentication.getName(), opponentUsername);
            gamesService.acceptGame(newGame.getId(), opponentUsername);
            return ResponseEntity.ok().body(Map.of("id", newGame.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<?> createGame(@RequestParam String opponentUsername, Authentication authentication) {
        try {
            Games newGame = gamesService.createGame(authentication.getName(), opponentUsername);
            return ResponseEntity.ok().body(Map.of("id", newGame.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/accept")
    @ResponseBody
    public ResponseEntity<String> acceptInvite(@RequestParam Integer gameId, Authentication authentication) {
        try {
            gamesService.acceptGame(gameId, authentication.getName());
            return ResponseEntity.ok("Приглашение принято");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/reject")
    @ResponseBody
    public ResponseEntity<String> rejectInvite(@RequestParam Integer gameId, Authentication authentication) {
        try {
            gamesService.rejectGame(gameId, authentication.getName());
            return ResponseEntity.ok("Приглашение отклонено");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/cancel")
    @ResponseBody
    public ResponseEntity<String> cancelInvite(@RequestParam Integer gameId, Authentication authentication) {
        try {
            gamesService.cancelGame(gameId, authentication.getName());
            return ResponseEntity.ok("Приглашение отменено");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/finish")
    @ResponseBody
    public ResponseEntity<String> finishGame(
            @RequestParam Integer gameId,
            @RequestParam Integer playerScore,
            @RequestParam Integer opponentScore,
            Authentication authentication) {
        try {
            // Добавим логирование для отладки
            log.info("Finishing game: gameId={}, playerScore={}, opponentScore={}", 
                    gameId, playerScore, opponentScore);
                    
            String username = authentication.getName();
            gamesService.finishGame(gameId, username, playerScore, opponentScore);
            return ResponseEntity.ok("Результат игры успешно сохранен");
        } catch (Exception e) {
            log.error("Error finishing game: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/finish-and-exit")
    @ResponseBody
    public ResponseEntity<String> finishGameAndExit(
            @RequestParam Integer gameId,
            Authentication authentication) {
        try {
            log.info("Finishing game and exit: gameId={}", gameId);
            String username = authentication.getName();
            gamesService.finishGameAndExit(gameId, username);
            return ResponseEntity.ok("Игра успешно завершена");
        } catch (Exception e) {
            log.error("Error finishing game and exit: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/early-finish")
    @ResponseBody
    public ResponseEntity<String> earlyFinishGame(
            @RequestParam Integer gameId,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            gamesService.finishGameAndExit(gameId, username);
            return ResponseEntity.ok("Игра успешно завершена");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{gameId}/messages")
    @ResponseBody
    public List<ChatMessage> getMessages(@PathVariable Integer gameId) {
        return chatService.getGameMessages(gameId);
    }

    @PostMapping("/{gameId}/messages")
    @ResponseBody
    public ResponseEntity<?> sendMessage(
            @PathVariable Integer gameId,
            @RequestParam String content,
            Authentication authentication) {
        try {
            ChatMessage message = chatService.saveMessage(gameId, authentication.getName(), content);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{gameId}/rounds")
    @ResponseBody
    public List<GameRound> getRounds(@PathVariable Integer gameId) {
        return gamesService.getGameRounds(gameId);
    }

    @GetMapping("/start/{gameId}")
    public String startGame(@PathVariable Integer gameId, Authentication authentication, Model model) {
        try {
            String username = authentication.getName();
            Games game = gamesService.startGame(gameId, username);
            List<ChatMessage> messages = chatService.getGameMessages(gameId);
            List<GameRound> rounds = gamesService.getGameRounds(gameId);
            model.addAttribute("game", game);
            model.addAttribute("messages", messages);
            model.addAttribute("rounds", rounds);
            model.addAttribute("currentUsername", username);
            model.addAttribute("themeSettings", themeSettingsService.getCurrentSettings());
            return "game";
        } catch (Exception e) {
            return "redirect:/profile?error=" + e.getMessage();
        }
    }

    @GetMapping("/history/{gameId}")
    public String showGameHistory(@PathVariable Integer gameId, Authentication authentication, Model model) {
        try {
            String username = authentication.getName();
            Games game = gamesService.getGameHistory(gameId, username);
            List<GameRound> rounds = gamesService.getGameRounds(gameId);
            
            model.addAttribute("game", game);
            model.addAttribute("rounds", rounds);
            model.addAttribute("themeSettings", themeSettingsService.getCurrentSettings());
            return "game_history";
        } catch (Exception e) {
            return "redirect:/profile?error=" + e.getMessage();
        }
    }

    @GetMapping("/history")
    public String showAllGamesHistory(Authentication authentication, Model model) {
        String username = authentication.getName();
        List<Games> finishedGames = gamesService.getAllFinishedGames(username);
        
        // Расчет статистики
        int totalGames = finishedGames.size();
        int totalWins = 0;
        double winPercentage = 0.0;

        for (Games game : finishedGames) {
            if (game.getPlayer().getUsername().equals(username)) {
                if (game.getPlayerPoints() > game.getOpponentPoints()) {
                    totalWins++;
                }
            } else {
                if (game.getOpponentPoints() > game.getPlayerPoints()) {
                    totalWins++;
                }
            }
        }

        // Расчет процента побед
        winPercentage = totalGames > 0 ? (double) totalWins / totalGames * 100 : 0.0;

        model.addAttribute("totalGames", totalGames);
        model.addAttribute("totalWins", totalWins);
        model.addAttribute("winPercentage", winPercentage);
        model.addAttribute("currentUsername", username);
        model.addAttribute("finishedGames", finishedGames);
        model.addAttribute("invitedPlayers", gamesService.getInvitedPlayers(username));
        model.addAttribute("sentInvites", gamesService.getSentInvites(username));
        model.addAttribute("themeSettings", themeSettingsService.getCurrentSettings());
        
        return "games_history";
    }

    @GetMapping("/{gameId}/chat")
    public String showChat(@PathVariable Integer gameId, Authentication authentication, Model model) {
        try {
            String username = authentication.getName();
            Games game = gamesService.getGameForChat(gameId, username);
            List<ChatMessage> messages = chatService.getGameMessages(gameId);
            
            // Отмечаем сообщения как прочитанные
            chatService.markMessagesAsRead(gameId, username);
            
            model.addAttribute("game", game);
            model.addAttribute("messages", messages);
            model.addAttribute("currentUsername", username);
            model.addAttribute("themeSettings", themeSettingsService.getCurrentSettings());
            return "chat";
        } catch (Exception e) {
            return "redirect:/profile?error=" + e.getMessage();
        }
    }
}