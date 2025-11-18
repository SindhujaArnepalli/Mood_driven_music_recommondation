package com.musicrecommender.service;

import com.musicrecommender.model.MoodScore;
import com.musicrecommender.model.UserInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Main service that combines sentiment analysis, rules engine, and time-context learning
 * to predict user mood
 */
@Service
public class MoodPredictionService {
    
    @Autowired
    private SentimentAnalysisService sentimentAnalysisService;
    
    @Autowired
    private RulesEngineService rulesEngineService;
    
    @Autowired
    private TimeContextLearningService timeContextLearningService;
    
    /**
     * Predicts mood based on all inputs
     */
    public MoodScore predictMood(UserInput userInput, String userId) {
        // Step 1: Sentiment analysis
        Map<String, Double> sentimentScores = sentimentAnalysisService.analyzeSentiment(userInput.getTextInput());
        
        // Step 2: Apply rules
        Map<String, Double> ruleAdjustments = rulesEngineService.applyRules(userInput, sentimentScores);
        
        // Step 3: Combine sentiment and rules (weighted combination)
        Map<String, Double> combinedMood = combineMoodScores(sentimentScores, ruleAdjustments);
        
        // Step 4: Apply time-context learning (if userId provided)
        Map<String, Double> finalMood;
        if (userId != null && !userId.isEmpty()) {
            finalMood = timeContextLearningService.adjustMoodWithLearning(
                userId, 
                userInput.getHourOfDay(), 
                combinedMood
            );
        } else {
            finalMood = combinedMood;
        }
        
        // Step 5: Determine primary mood
        String primaryMood = determinePrimaryMood(finalMood);
        Double confidence = finalMood.getOrDefault(primaryMood.toLowerCase(), 0.0);
        
        return new MoodScore(primaryMood, confidence, finalMood);
    }
    
    /**
     * Combines sentiment scores with rule adjustments
     */
    private Map<String, Double> combineMoodScores(
            Map<String, Double> sentimentScores, 
            Map<String, Double> ruleAdjustments) {
        
        Map<String, Double> combined = new HashMap<>();
        
        // Map sentiment scores to mood categories
        double stressScore = sentimentScores.getOrDefault("stress", 0.0);
        double negativeScore = sentimentScores.getOrDefault("negative", 0.0);
        double focusScore = sentimentScores.getOrDefault("focus", 0.0);
        double energyScore = sentimentScores.getOrDefault("energy", 0.0);
        double positiveScore = sentimentScores.getOrDefault("positive", 0.0);
        
        // Combine with rule adjustments
        combined.put("tired", ruleAdjustments.getOrDefault("tired", 0.0) + 
                     (negativeScore * 0.3));
        combined.put("stressed", ruleAdjustments.getOrDefault("stressed", 0.0) + 
                     (stressScore * 0.7) + (negativeScore * 0.3));
        combined.put("energetic", ruleAdjustments.getOrDefault("energetic", 0.0) + 
                     (energyScore * 0.7) + (positiveScore * 0.3));
        combined.put("relaxed", ruleAdjustments.getOrDefault("relaxed", 0.0) + 
                     (positiveScore * 0.5));
        combined.put("focused", ruleAdjustments.getOrDefault("focused", 0.0) + 
                     (focusScore * 0.7));
        combined.put("anxious", ruleAdjustments.getOrDefault("anxious", 0.0) + 
                     (stressScore * 0.5));
        
        // Normalize all values to 0.0-1.0
        combined.replaceAll((k, v) -> Math.min(1.0, Math.max(0.0, v)));
        
        return combined;
    }
    
    /**
     * Determines the primary mood from mood distribution
     */
    private String determinePrimaryMood(Map<String, Double> moodDistribution) {
        return moodDistribution.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("relaxed")
            .toUpperCase();
    }
}

