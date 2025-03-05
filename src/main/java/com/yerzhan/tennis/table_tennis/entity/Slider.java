package com.yerzhan.tennis.table_tennis.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "slider_images")
@NoArgsConstructor
@AllArgsConstructor
public class Slider {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "image_url", nullable = false)
    private String imageUrl;
    
    @Column(name = "display_order")
    private Integer displayOrder;
    
    @Column(name = "is_active")
    private boolean isActive = true;
} 