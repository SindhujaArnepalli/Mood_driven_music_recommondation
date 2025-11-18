package com.musicrecommender.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MoodScore {
    private String primaryMood; // e.g., "tired", "stressed", "energetic", "relaxed"
    private Double confidence; // 0.0 to 1.0
    private Map<String, Double> moodDistribution; // All mood scores
    
    // Mood categories
    public enum MoodCategory {
        TIRED, STRESSED, ENERGETIC, RELAXED, HAPPY, SAD, FOCUSED, ANXIOUS
    }
}

