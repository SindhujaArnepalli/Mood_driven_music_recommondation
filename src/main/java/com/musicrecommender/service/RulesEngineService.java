package com.musicrecommender.service;

import com.musicrecommender.model.UserInput;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

/**
 * Rules-based engine for mood prediction
 * Applies business rules based on typing speed, time of day, and text patterns
 */
@Service
public class RulesEngineService {
    
    // Typing speed thresholds (characters per second)
    private static final double VERY_SLOW_THRESHOLD = 1.0;
    private static final double SLOW_THRESHOLD = 2.0;
    private static final double NORMAL_THRESHOLD = 4.0;
    private static final double FAST_THRESHOLD = 6.0;
    
    /**
     * Applies rules to determine mood adjustments based on context
     */
    public Map<String, Double> applyRules(UserInput userInput, Map<String, Double> sentimentScores) {
        Map<String, Double> ruleAdjustments = new HashMap<>();
        
        // Initialize adjustments
        ruleAdjustments.put("tired", 0.0);
        ruleAdjustments.put("stressed", 0.0);
        ruleAdjustments.put("energetic", 0.0);
        ruleAdjustments.put("relaxed", 0.0);
        ruleAdjustments.put("focused", 0.0);
        ruleAdjustments.put("anxious", 0.0);
        
        // Rule 1: Typing speed analysis
        double typingSpeed = userInput.getTypingSpeed();
        if (typingSpeed < VERY_SLOW_THRESHOLD) {
            // Very slow typing suggests tiredness or distraction
            ruleAdjustments.put("tired", ruleAdjustments.get("tired") + 0.4);
            ruleAdjustments.put("stressed", ruleAdjustments.get("stressed") + 0.2);
        } else if (typingSpeed < SLOW_THRESHOLD) {
            // Slow typing suggests tiredness or careful thought
            ruleAdjustments.put("tired", ruleAdjustments.get("tired") + 0.3);
            ruleAdjustments.put("focused", ruleAdjustments.get("focused") + 0.1);
        } else if (typingSpeed > FAST_THRESHOLD) {
            // Fast typing suggests energy or urgency
            ruleAdjustments.put("energetic", ruleAdjustments.get("energetic") + 0.3);
            ruleAdjustments.put("stressed", ruleAdjustments.get("stressed") + 0.2);
        }
        
        // Rule 2: Time of day analysis
        int hour = userInput.getHourOfDay();
        
        // Late night (11 PM - 4 AM)
        if (hour >= 23 || hour < 4) {
            ruleAdjustments.put("tired", ruleAdjustments.get("tired") + 0.5);
            ruleAdjustments.put("stressed", ruleAdjustments.get("stressed") + 0.2);
        }
        // Early morning (4 AM - 7 AM)
        else if (hour >= 4 && hour < 7) {
            ruleAdjustments.put("tired", ruleAdjustments.get("tired") + 0.4);
        }
        // Morning (7 AM - 12 PM)
        else if (hour >= 7 && hour < 12) {
            ruleAdjustments.put("energetic", ruleAdjustments.get("energetic") + 0.2);
            ruleAdjustments.put("focused", ruleAdjustments.get("focused") + 0.2);
        }
        // Afternoon (12 PM - 5 PM)
        else if (hour >= 12 && hour < 17) {
            ruleAdjustments.put("focused", ruleAdjustments.get("focused") + 0.1);
        }
        // Evening (5 PM - 11 PM)
        else if (hour >= 17 && hour < 23) {
            ruleAdjustments.put("relaxed", ruleAdjustments.get("relaxed") + 0.2);
        }
        
        // Rule 3: Text pattern analysis (combine with sentiment)
        String text = userInput.getTextInput().toLowerCase();
        
        // Study-related text
        if (text.contains("study") || text.contains("studying") || 
            text.contains("exam") || text.contains("test")) {
            ruleAdjustments.put("focused", ruleAdjustments.get("focused") + 0.3);
            ruleAdjustments.put("stressed", ruleAdjustments.get("stressed") + 0.2);
        }
        
        // Short, fragmented text suggests tiredness
        if (text.split("\\s+").length < 3) {
            ruleAdjustments.put("tired", ruleAdjustments.get("tired") + 0.2);
        }
        
        // Exclamation marks suggest energy or stress
        long exclamationCount = text.chars().filter(ch -> ch == '!').count();
        if (exclamationCount > 2) {
            ruleAdjustments.put("energetic", ruleAdjustments.get("energetic") + 0.2);
            ruleAdjustments.put("stressed", ruleAdjustments.get("stressed") + 0.1);
        }
        
        // Combine with sentiment scores
        if (sentimentScores.get("stress") > 0.3) {
            ruleAdjustments.put("stressed", ruleAdjustments.get("stressed") + 0.3);
            ruleAdjustments.put("anxious", ruleAdjustments.get("anxious") + 0.2);
        }
        
        if (sentimentScores.get("focus") > 0.3) {
            ruleAdjustments.put("focused", ruleAdjustments.get("focused") + 0.3);
        }
        
        if (sentimentScores.get("energy") > 0.3) {
            ruleAdjustments.put("energetic", ruleAdjustments.get("energetic") + 0.3);
        }
        
        // Normalize all values to 0.0-1.0 range
        ruleAdjustments.replaceAll((k, v) -> Math.min(1.0, v));
        
        return ruleAdjustments;
    }
}

