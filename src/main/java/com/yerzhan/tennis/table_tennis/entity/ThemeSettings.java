package com.yerzhan.tennis.table_tennis.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "theme_settings")
@NoArgsConstructor
@AllArgsConstructor
public class ThemeSettings {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "background_image_url")
    private String backgroundImageUrl;
    
    @Version
    private Long version;
} 