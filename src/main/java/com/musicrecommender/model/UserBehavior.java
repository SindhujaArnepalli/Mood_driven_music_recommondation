package com.musicrecommender.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Stores user behavior patterns for time-context learning
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBehavior {
    private String userId;
    private LocalDateTime timestamp;
    private Map<Integer, Double> hourMoodPatterns; // Hour -> Mood score mapping
    private Map<String, Integer> tagFrequency; // Search tag -> frequency
    private Double averageTypingSpeed;
    private Map<String, Integer> moodHistory; // Mood -> count
}

