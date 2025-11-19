package com.musicrecommender.service;

import com.musicrecommender.model.UserBehavior;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Time-context behavior learning service
 * Learns patterns from user behavior over time
 */
@Service
public class TimeContextLearningService {
    
    // In-memory storage for user behaviors (in production, use a database)
    private final Map<String, List<UserBehavior>> userBehaviorHistory = new ConcurrentHashMap<>();
    
    /**
     * Records user behavior for learning
     */
    public void recordBehavior(String userId, UserBehavior behavior) {
        userBehaviorHistory.computeIfAbsent(userId, k -> new ArrayList<>()).add(behavior);
        
        // Keep only last 100 behaviors per user to prevent memory issues
        List<UserBehavior> behaviors = userBehaviorHistory.get(userId);
        if (behaviors.size() > 100) {
            behaviors.remove(0);
        }
    }
    
    /**
     * Gets learned patterns for a user at a specific hour
     */
    public Map<String, Double> getLearnedPatterns(String userId, int hour) {
        List<UserBehavior> behaviors = userBehaviorHistory.getOrDefault(userId, new ArrayList<>());
        
        if (behaviors.isEmpty()) {
            return getDefaultPatterns();
        }
        
        Map<String, Double> patterns = new HashMap<>();
        patterns.put("tired", 0.0);
        patterns.put("stressed", 0.0);
        patterns.put("energetic", 0.0);
        patterns.put("relaxed", 0.0);
        patterns.put("focused", 0.0);
        
        // Find behaviors at similar hours (±2 hours)
        List<UserBehavior> relevantBehaviors = behaviors.stream()
            .filter(b -> {
                int behaviorHour = b.getTimestamp().getHour();
                return Math.abs(behaviorHour - hour) <= 2;
            })
            .toList();
        
        if (relevantBehaviors.isEmpty()) {
            return getDefaultPatterns();
        }
        
        // Aggregate mood patterns
        Map<String, Integer> moodCounts = new HashMap<>();
        AtomicInteger totalCount = new AtomicInteger();
        
        for (UserBehavior behavior : relevantBehaviors) {
            if (behavior.getMoodHistory() != null) {
                behavior.getMoodHistory().forEach((mood, count) -> {
                    String normalizedMood = mood.toLowerCase();
                    moodCounts.put(normalizedMood, moodCounts.getOrDefault(normalizedMood, 0) + count);
                    totalCount.addAndGet(count);
                });
            }
        }
        
        // Convert to probabilities
        if (totalCount.get() > 0) {
            moodCounts.forEach((mood, count) -> {
                patterns.put(mood, (double) count / totalCount.get());
            });
        }
        
        return patterns;
    }
    
    /**
     * Gets learned typing speed patterns
     */
    public Double getAverageTypingSpeed(String userId) {
        List<UserBehavior> behaviors = userBehaviorHistory.getOrDefault(userId, new ArrayList<>());
        
        if (behaviors.isEmpty()) {
            return 3.0; // Default average typing speed
        }
        
        return behaviors.stream()
            .filter(b -> b.getAverageTypingSpeed() != null)
            .mapToDouble(UserBehavior::getAverageTypingSpeed)
            .average()
            .orElse(3.0);
    }
    
    /**
     * Gets popular search tags for a user
     */
    public Map<String, Integer> getPopularTags(String userId) {
        List<UserBehavior> behaviors = userBehaviorHistory.getOrDefault(userId, new ArrayList<>());
        
        Map<String, Integer> tagFrequency = new HashMap<>();
        
        for (UserBehavior behavior : behaviors) {
            if (behavior.getTagFrequency() != null) {
                behavior.getTagFrequency().forEach((tag, freq) -> {
                    tagFrequency.put(tag, tagFrequency.getOrDefault(tag, 0) + freq);
                });
            }
        }
        
        return tagFrequency;
    }
    
    /**
     * Adjusts mood predictions based on learned patterns
     */
    public Map<String, Double> adjustMoodWithLearning(String userId, int hour, Map<String, Double> baseMood) {
        Map<String, Double> learnedPatterns = getLearnedPatterns(userId, hour);
        
        // Blend learned patterns with base mood (70% base, 30% learned)
        Map<String, Double> adjustedMood = new HashMap<>();
        
        Set<String> allMoods = new HashSet<>(baseMood.keySet());
        allMoods.addAll(learnedPatterns.keySet());
        
        for (String mood : allMoods) {
            double baseValue = baseMood.getOrDefault(mood, 0.0);
            double learnedValue = learnedPatterns.getOrDefault(mood, 0.0);
            adjustedMood.put(mood, baseValue * 0.7 + learnedValue * 0.3);
        }
        
        return adjustedMood;
    }
    
    private Map<String, Double> getDefaultPatterns() {
        Map<String, Double> defaults = new HashMap<>();
        defaults.put("tired", 0.0);
        defaults.put("stressed", 0.0);
        defaults.put("energetic", 0.0);
        defaults.put("relaxed", 0.0);
        defaults.put("focused", 0.0);
        return defaults;
    }
}

