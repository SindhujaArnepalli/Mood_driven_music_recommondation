package com.musicrecommender.service;

import org.springframework.stereotype.Service;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Lightweight ML-based sentiment analysis service
 * Uses keyword matching, sentiment dictionaries, and simple scoring
 */
@Service
public class SentimentAnalysisService {
    
    // Positive sentiment keywords
    private static final Set<String> POSITIVE_WORDS = Set.of(
        "happy", "great", "awesome", "amazing", "love", "excited", "good", "nice",
        "wonderful", "fantastic", "excellent", "perfect", "best", "yeah", "yes"
    );
    
    // Negative sentiment keywords
    private static final Set<String> NEGATIVE_WORDS = Set.of(
        "sad", "bad", "hate", "terrible", "awful", "worst", "angry", "frustrated",
        "tired", "exhausted", "stressed", "anxious", "worried", "depressed", "sick"
    );
    
    // Stress indicators
    private static final Set<String> STRESS_WORDS = Set.of(
        "stress", "stressed", "pressure", "deadline", "exam", "test", "work", "busy",
        "overwhelmed", "fr", "fuck", "damn", "ugh", "argh"
    );
    
    // Study/focus indicators
    private static final Set<String> FOCUS_WORDS = Set.of(
        "study", "studying", "focus", "concentrate", "work", "homework", "assignment",
        "reading", "learning", "exam", "test"
    );
    
    // Energy indicators
    private static final Set<String> ENERGY_WORDS = Set.of(
        "energy", "energetic", "pumped", "ready", "go", "let's", "party", "dance",
        "workout", "exercise", "run", "gym"
    );
    
    /**
     * Analyzes text sentiment and returns mood scores
     */
    public Map<String, Double> analyzeSentiment(String text) {
        if (text == null || text.trim().isEmpty()) {
            return getNeutralSentiment();
        }
        
        String lowerText = text.toLowerCase();
        String[] words = lowerText.split("\\s+");
        
        Map<String, Double> sentimentScores = new HashMap<>();
        sentimentScores.put("positive", 0.0);
        sentimentScores.put("negative", 0.0);
        sentimentScores.put("stress", 0.0);
        sentimentScores.put("focus", 0.0);
        sentimentScores.put("energy", 0.0);
        
        // Count word matches
        int positiveCount = 0;
        int negativeCount = 0;
        int stressCount = 0;
        int focusCount = 0;
        int energyCount = 0;
        
        for (String word : words) {
            // Remove punctuation
            word = word.replaceAll("[^a-z]", "");
            
            if (POSITIVE_WORDS.contains(word)) {
                positiveCount++;
            }
            if (NEGATIVE_WORDS.contains(word)) {
                negativeCount++;
            }
            if (STRESS_WORDS.contains(word)) {
                stressCount++;
            }
            if (FOCUS_WORDS.contains(word)) {
                focusCount++;
            }
            if (ENERGY_WORDS.contains(word)) {
                energyCount++;
            }
        }
        
        // Normalize scores (0.0 to 1.0)
        int totalWords = words.length;
        if (totalWords > 0) {
            sentimentScores.put("positive", Math.min(1.0, positiveCount * 0.3));
            sentimentScores.put("negative", Math.min(1.0, negativeCount * 0.3));
            sentimentScores.put("stress", Math.min(1.0, stressCount * 0.4));
            sentimentScores.put("focus", Math.min(1.0, focusCount * 0.4));
            sentimentScores.put("energy", Math.min(1.0, energyCount * 0.4));
        }
        
        // Check for intensifiers (very, really, so, etc.)
        if (lowerText.contains("very") || lowerText.contains("really") || 
            lowerText.contains("so ") || lowerText.contains("extremely")) {
            sentimentScores.replaceAll((k, v) -> v * 1.2);
        }
        
        // Check for negation
        if (lowerText.contains("not ") || lowerText.contains("no ") || 
            lowerText.contains("don't") || lowerText.contains("can't")) {
            sentimentScores.put("negative", Math.min(1.0, sentimentScores.get("negative") + 0.2));
        }
        
        return sentimentScores;
    }
    
    private Map<String, Double> getNeutralSentiment() {
        Map<String, Double> neutral = new HashMap<>();
        neutral.put("positive", 0.0);
        neutral.put("negative", 0.0);
        neutral.put("stress", 0.0);
        neutral.put("focus", 0.0);
        neutral.put("energy", 0.0);
        return neutral;
    }
}

