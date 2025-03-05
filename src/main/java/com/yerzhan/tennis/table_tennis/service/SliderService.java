package com.yerzhan.tennis.table_tennis.service;

import com.yerzhan.tennis.table_tennis.entity.Slider;
import com.yerzhan.tennis.table_tennis.repository.SliderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SliderService {
    
    private final SliderRepository sliderRepository;
    private final FileStorageService fileStorageService;
    
    public List<Slider> getAllSlides() {
        return sliderRepository.findAll();
    }
    
    public List<Slider> getActiveSlides() {
        return sliderRepository.findByIsActiveTrueOrderByDisplayOrderAsc();
    }
    
    public Slider addSlide(MultipartFile image, Integer displayOrder) throws Exception {
        String imageUrl = fileStorageService.storeFile(image);
        
        Slider slider = new Slider();
        slider.setImageUrl(imageUrl);
        slider.setDisplayOrder(displayOrder);
        slider.setActive(true);
        
        return sliderRepository.save(slider);
    }
    
    public void deleteSlide(Integer id) {
        sliderRepository.deleteById(id);
    }
    
    public void toggleSlideStatus(Integer id) {
        Slider slider = sliderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Слайд не найден"));
        slider.setActive(!slider.isActive());
        sliderRepository.save(slider);
    }
    
    public void updateDisplayOrder(Integer id, Integer newOrder) {
        Slider slider = sliderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Слайд не найден"));
        slider.setDisplayOrder(newOrder);
        sliderRepository.save(slider);
    }
} 