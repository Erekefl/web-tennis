package com.yerzhan.tennis.table_tennis.repository;

import com.yerzhan.tennis.table_tennis.entity.Slider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SliderRepository extends JpaRepository<Slider, Integer> {
    List<Slider> findByIsActiveTrueOrderByDisplayOrderAsc();
} 