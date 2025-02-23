package com.yerzhan.tennis.table_tennis.repository;

import com.yerzhan.tennis.table_tennis.entity.ThemeSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThemeSettingsRepository extends JpaRepository<ThemeSettings, Integer> {
    ThemeSettings findFirstByOrderByIdAsc();
} 