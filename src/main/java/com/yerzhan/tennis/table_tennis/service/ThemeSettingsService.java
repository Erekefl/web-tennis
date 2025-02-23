package com.yerzhan.tennis.table_tennis.service;

import com.yerzhan.tennis.table_tennis.entity.ThemeSettings;
import com.yerzhan.tennis.table_tennis.repository.ThemeSettingsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ThemeSettingsService {

    private final ThemeSettingsRepository themeSettingsRepository;
    private ThemeSettings cachedSettings;

    public ThemeSettings getCurrentSettings() {
        if (cachedSettings == null) {
            cachedSettings = themeSettingsRepository.findFirstByOrderByIdAsc();
            if (cachedSettings == null) {
                cachedSettings = new ThemeSettings();
                cachedSettings = themeSettingsRepository.save(cachedSettings);
            }
        }
        return cachedSettings;
    }

    @Transactional
    public void updateSettings(String backgroundImageUrl) {
        ThemeSettings settings = getCurrentSettings();
        settings.setBackgroundImageUrl(backgroundImageUrl);
        cachedSettings = themeSettingsRepository.save(settings);
    }
} 