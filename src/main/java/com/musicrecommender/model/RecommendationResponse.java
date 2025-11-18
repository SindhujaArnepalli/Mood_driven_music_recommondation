package com.musicrecommender.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationResponse {
    private MoodScore moodScore;
    private List<MusicCategory> recommendedCategories;
    private Playlist playlist;
    private String reasoning; // Explanation of why these recommendations were made
}

