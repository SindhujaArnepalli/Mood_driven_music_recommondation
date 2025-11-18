package com.musicrecommender.controller;

import com.musicrecommender.model.*;
import com.musicrecommender.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST API controller for mood-based music recommendations
 */
@RestController
@RequestMapping("/api/recommendations")
@CrossOrigin(origins = "*") // Allow CORS for frontend integration
public class RecommendationController {
    
    @Autowired
    private MoodPredictionService moodPredictionService;
    
    @Autowired
    private RecommendationEngineService recommendationEngineService;
    
    @Autowired
    private PlaylistGeneratorService playlistGeneratorService;
    
    @Autowired
    private TimeContextLearningService timeContextLearningService;
    
    /**
     * Main endpoint: Get music recommendations based on user input
     * 
     * Example request:
     * POST /api/recommendations
     * {
     *   "textInput": "studying fr today",
     *   "typingSpeed": 1.5,
     *   "timeOfDay": "2024-01-15T02:00:00",
     *   "searchHistoryTags": ["study", "focus"]
     * }
     */
    @PostMapping
    public ResponseEntity<RecommendationResponse> getRecommendations(
            @Valid @RequestBody UserInput userInput,
            @RequestParam(required = false) String userId,
            @RequestParam(defaultValue = "30") int playlistLengthMinutes) {
        
        // Predict mood
        MoodScore moodScore = moodPredictionService.predictMood(userInput, userId);
        
        // Get music category recommendations
        var recommendedCategories = recommendationEngineService.getRecommendations(moodScore);
        
        // Generate playlist
        Playlist playlist = playlistGeneratorService.generatePlaylist(
            moodScore, 
            recommendedCategories, 
            playlistLengthMinutes
        );
        
        // Generate reasoning
        String reasoning = recommendationEngineService.generateReasoning(moodScore, recommendedCategories);
        
        // Record behavior for learning (if userId provided)
        if (userId != null && !userId.isEmpty()) {
            recordUserBehavior(userId, userInput, moodScore);
        }
        
        RecommendationResponse response = new RecommendationResponse(
            moodScore,
            recommendedCategories,
            playlist,
            reasoning
        );
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get mood prediction only (without recommendations)
     */
    @PostMapping("/mood")
    public ResponseEntity<MoodScore> predictMood(
            @Valid @RequestBody UserInput userInput,
            @RequestParam(required = false) String userId) {
        
        MoodScore moodScore = moodPredictionService.predictMood(userInput, userId);
        return ResponseEntity.ok(moodScore);
    }
    
    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Mood-Driven Music Recommendation Engine");
        return ResponseEntity.ok(response);
    }
    
    /**
     * Records user behavior for learning
     */
    private void recordUserBehavior(String userId, UserInput userInput, MoodScore moodScore) {
        UserBehavior behavior = new UserBehavior();
        behavior.setUserId(userId);
        behavior.setTimestamp(userInput.getTimeOfDay());
        
        // Create mood history
        Map<String, Integer> moodHistory = new HashMap<>();
        moodHistory.put(moodScore.getPrimaryMood(), 1);
        behavior.setMoodHistory(moodHistory);
        
        // Create tag frequency from search history
        if (userInput.getSearchHistoryTags() != null) {
            Map<String, Integer> tagFrequency = new HashMap<>();
            for (String tag : userInput.getSearchHistoryTags()) {
                tagFrequency.put(tag, tagFrequency.getOrDefault(tag, 0) + 1);
            }
            behavior.setTagFrequency(tagFrequency);
        }
        
        behavior.setAverageTypingSpeed(userInput.getTypingSpeed());
        
        timeContextLearningService.recordBehavior(userId, behavior);
    }
}

