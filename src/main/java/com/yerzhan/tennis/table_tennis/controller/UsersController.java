package com.yerzhan.tennis.table_tennis.controller;

import com.yerzhan.tennis.table_tennis.dto.UserDTO;
import com.yerzhan.tennis.table_tennis.entity.Users;
import com.yerzhan.tennis.table_tennis.service.impl.UserDetailServiceImpl;
import com.yerzhan.tennis.table_tennis.service.impl.GamesService;
import com.yerzhan.tennis.table_tennis.service.ThemeSettingsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UsersController {

    private final UserDetailServiceImpl userDetailService;
    private final GamesService gamesService;
    private final ThemeSettingsService themeSettingsService;

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("themeSettings", themeSettingsService.getCurrentSettings());
        return "login";
    }

    @GetMapping("/profile")
    public String showProfile(Authentication authentication, Model model) {
        try {
            // Всегда используем имя текущего аутентифицированного пользователя
            String username = authentication.getName();
            Users user = userDetailService.findByUsername(username);
            
            // Добавляем данные в модель
            model.addAttribute("currentUser", user);
            model.addAttribute("username", username);
            model.addAttribute("invitedPlayers", gamesService.getInvitedPlayers(username));
            model.addAttribute("sentInvites", gamesService.getSentInvites(username));
            model.addAttribute("themeSettings", themeSettingsService.getCurrentSettings());
            
            return "profile";
        } catch (Exception e) {
            // В случае ошибки перенаправляем на страницу логина
            return "redirect:/login?error=profile";
        }
    }

    @GetMapping("/profile/invites")
    public String getInvitedPlayers(Authentication authentication, Model model) {
        String username = authentication.getName();
        log.info("Запрос на получение списка приглашений для пользователя: {}", username);
        var invitedPlayers = gamesService.getInvitedPlayers(username);
        log.info("Найдено {} приглашений для пользователя {}", invitedPlayers.size(), username);
        model.addAttribute("invitedPlayers", invitedPlayers);
        return "profile :: invitedPlayersList";
    }

    @GetMapping("/profile/sent-invites")
    public String getSentInvites(Authentication authentication, Model model) {
        String username = authentication.getName();
        log.info("Запрос на получение списка отправленных приглашений для пользователя: {}", username);
        var sentInvites = gamesService.getSentInvites(username);
        log.info("Найдено {} отправленных приглашений для пользователя {}", sentInvites.size(), username);
        model.addAttribute("sentInvites", sentInvites);
        return "profile :: sentInvitesList";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("userDTO", new UserDTO());
        model.addAttribute("themeSettings", themeSettingsService.getCurrentSettings());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute @Valid UserDTO userDTO, 
                             BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("themeSettings", themeSettingsService.getCurrentSettings());
            return "register";
        }

        try {
            userDetailService.createUser(userDTO);
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка регистрации: " + e.getMessage());
            model.addAttribute("themeSettings", themeSettingsService.getCurrentSettings());
            return "register";
        }
    }

    @GetMapping("/players")
    public String showAllPlayers(Authentication authentication, Model model) {
        String currentUsername = authentication.getName();
        List<Users> players = userDetailService.getAllUsers().stream()
                .filter(user -> !user.getUsername().equals(currentUsername))
                .toList();
        model.addAttribute("players", players);
        model.addAttribute("themeSettings", themeSettingsService.getCurrentSettings());
        return "players";
    }

}