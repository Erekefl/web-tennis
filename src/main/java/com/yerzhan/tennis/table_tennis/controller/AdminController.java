package com.yerzhan.tennis.table_tennis.controller;

import com.yerzhan.tennis.table_tennis.dto.AdminUpdateUserDTO;
import com.yerzhan.tennis.table_tennis.service.impl.UserDetailServiceImpl;
import com.yerzhan.tennis.table_tennis.service.ThemeSettingsService;
import com.yerzhan.tennis.table_tennis.service.FileStorageService;
import com.yerzhan.tennis.table_tennis.service.SliderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserDetailServiceImpl userService;
    private final ThemeSettingsService themeSettingsService;
    private final FileStorageService fileStorageService;
    private final SliderService sliderService;

    @GetMapping
    public String adminPage(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("slides", sliderService.getAllSlides());
        model.addAttribute("themeSettings", themeSettingsService.getCurrentSettings());
        return "admin/admin";
    }

    @PostMapping("/users/update")
    public String updateUser(@ModelAttribute("user") @Valid AdminUpdateUserDTO dto,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", 
                "Ошибка валидации: " + bindingResult.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/admin";
        }

        try {
            userService.updateUser(dto);
            redirectAttributes.addFlashAttribute("success", 
                "Пользователь " + dto.getUsername() + " успешно обновлен");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Ошибка при обновлении пользователя: " + e.getMessage());
        }

        return "redirect:/admin";
    }

    @PostMapping("/users/update-role")
    public String updateUserRole(@RequestParam Integer userId,
                               @RequestParam String role,
                               RedirectAttributes redirectAttributes) {
        try {
            userService.updateUserRole(userId, role);
            redirectAttributes.addFlashAttribute("success", "Роль пользователя успешно обновлена");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Ошибка при обновлении роли пользователя: " + e.getMessage());
        }
        return "redirect:/admin";
    }

    @PostMapping("/users/delete")
    public String deleteUser(@RequestParam Integer userId,
                           RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(userId);
            redirectAttributes.addFlashAttribute("success", "Пользователь успешно удален");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Ошибка при удалении пользователя: " + e.getMessage());
        }
        return "redirect:/admin";
    }

    @PostMapping("/theme/update")
    public String updateTheme(@RequestParam(required = false) MultipartFile backgroundImage,
                            RedirectAttributes redirectAttributes) {
        try {
            String backgroundImageUrl = null;
            if (backgroundImage != null && !backgroundImage.isEmpty()) {
                backgroundImageUrl = fileStorageService.storeFile(backgroundImage);
            }
            
            themeSettingsService.updateSettings(backgroundImageUrl);
            redirectAttributes.addFlashAttribute("success", "Фоновое изображение успешно обновлено");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Ошибка при обновлении фонового изображения: " + e.getMessage());
        }
        return "redirect:/admin";
    }

    @PostMapping("/slider/add")
    public String addSlide(@RequestParam("image") MultipartFile image,
                          @RequestParam(value = "displayOrder", required = false) Integer displayOrder,
                          RedirectAttributes redirectAttributes) {
        try {
            sliderService.addSlide(image, displayOrder);
            redirectAttributes.addFlashAttribute("success", "Слайд успешно добавлен");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Ошибка при добавлении слайда: " + e.getMessage());
        }
        return "redirect:/admin";
    }

    @PostMapping("/slider/delete/{id}")
    public String deleteSlide(@PathVariable Integer id,
                            RedirectAttributes redirectAttributes) {
        try {
            sliderService.deleteSlide(id);
            redirectAttributes.addFlashAttribute("success", "Слайд успешно удален");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Ошибка при удалении слайда: " + e.getMessage());
        }
        return "redirect:/admin";
    }

    @PostMapping("/slider/toggle/{id}")
    public String toggleSlideStatus(@PathVariable Integer id,
                                  RedirectAttributes redirectAttributes) {
        try {
            sliderService.toggleSlideStatus(id);
            redirectAttributes.addFlashAttribute("success", "Статус слайда успешно изменен");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Ошибка при изменении статуса слайда: " + e.getMessage());
        }
        return "redirect:/admin";
    }

    @PostMapping("/slider/order")
    public String updateSlideOrder(@RequestParam Integer slideId,
                                 @RequestParam Integer newOrder,
                                 RedirectAttributes redirectAttributes) {
        try {
            sliderService.updateDisplayOrder(slideId, newOrder);
            redirectAttributes.addFlashAttribute("success", "Порядок слайдов успешно обновлен");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Ошибка при обновлении порядка слайдов: " + e.getMessage());
        }
        return "redirect:/admin";
    }
} 