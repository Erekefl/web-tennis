package com.yerzhan.tennis.table_tennis.controller;

import com.yerzhan.tennis.table_tennis.dto.PlayerStatsDTO;
import com.yerzhan.tennis.table_tennis.service.ThemeSettingsService;
import com.yerzhan.tennis.table_tennis.service.impl.GamesService;
import com.yerzhan.tennis.table_tennis.service.SliderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class WelcomeController {

    private final ThemeSettingsService themeSettingsService;
    private final GamesService gamesService;
    private final SliderService sliderService;

    @GetMapping("/")
    public String showWelcomePage(Model model, Authentication authentication) {
        model.addAttribute("themeSettings", themeSettingsService.getCurrentSettings());
        model.addAttribute("slides", sliderService.getActiveSlides());
        model.addAttribute("isAuthenticated", authentication != null);
        return "welcome";
    }

    @GetMapping("/rules")
    public String showRules(Model model) {
        model.addAttribute("themeSettings", themeSettingsService.getCurrentSettings());
        return "rules";
    }

    @GetMapping("/rating")
    public String showRating(Model model) {
        List<PlayerStatsDTO> players = gamesService.getPlayersStats();
        model.addAttribute("players", players);
        model.addAttribute("themeSettings", themeSettingsService.getCurrentSettings());
        return "rating";
    }

    @GetMapping("/guest")
    public String showGuestMode(Model model) {
        model.addAttribute("themeSettings", themeSettingsService.getCurrentSettings());
        return "guest";
    }
} 