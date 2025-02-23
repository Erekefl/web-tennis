package com.yerzhan.tennis.table_tennis.controller;

import com.yerzhan.tennis.table_tennis.service.ThemeSettingsService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
public class CustomErrorController implements ErrorController {

    private final ThemeSettingsService themeSettingsService;

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        // Получаем статус ошибки
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        
        model.addAttribute("errorCode", status != null ? status.toString() : "Unknown");
        model.addAttribute("errorMessage", message != null ? message.toString() : "Произошла неизвестная ошибка");
        model.addAttribute("themeSettings", themeSettingsService.getCurrentSettings());
        
        return "error";
    }
} 