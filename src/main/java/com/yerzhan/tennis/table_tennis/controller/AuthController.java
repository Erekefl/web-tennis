package com.yerzhan.tennis.table_tennis.controller;

import com.yerzhan.tennis.table_tennis.entity.Users;
import com.yerzhan.tennis.table_tennis.model.ConfirmationToken;
import com.yerzhan.tennis.table_tennis.model.TokenType;
import com.yerzhan.tennis.table_tennis.repository.ConfirmationTokenRepository;
import com.yerzhan.tennis.table_tennis.repository.UserRepository;
import com.yerzhan.tennis.table_tennis.service.EmailService;
import com.yerzhan.tennis.table_tennis.service.ThemeSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final ConfirmationTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final ThemeSettingsService themeSettingsService;

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm(Model model) {
        model.addAttribute("themeSettings", themeSettingsService.getCurrentSettings());
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, 
                                      RedirectAttributes redirectAttributes) {
        try {
            Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь с таким email не найден"));

            // Создаем токен для сброса пароля
            ConfirmationToken token = new ConfirmationToken(user, TokenType.PASSWORD_RESET);
            tokenRepository.save(token);

            // Отправляем email
            emailService.sendPasswordResetEmail(email, token.getToken());

            return "redirect:/auth/check-email";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/auth/forgot-password";
        }
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, 
                                      Model model) {
        try {
            ConfirmationToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Недействительный токен"));

            if (resetToken.isExpired()) {
                throw new RuntimeException("Срок действия токена истек");
            }

            model.addAttribute("token", token);
            model.addAttribute("themeSettings", themeSettingsService.getCurrentSettings());
            return "reset-password";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token,
                                     @RequestParam("password") String password,
                                     RedirectAttributes redirectAttributes) {
        try {
            ConfirmationToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Недействительный токен"));

            if (resetToken.isExpired()) {
                throw new RuntimeException("Срок действия токена истек");
            }

            Users user = resetToken.getUser();
            user.setPassword(passwordEncoder.encode(password));
            userRepository.save(user);

            // Подтверждаем использование токена
            resetToken.setConfirmedAt(java.time.LocalDateTime.now());
            tokenRepository.save(resetToken);

            redirectAttributes.addFlashAttribute("message", "Пароль успешно изменен");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/auth/reset-password?token=" + token;
        }
    }

    @GetMapping("/check-email")
    public String showCheckEmailPage(Model model) {
        model.addAttribute("themeSettings", themeSettingsService.getCurrentSettings());
        return "check-email";
    }
} 