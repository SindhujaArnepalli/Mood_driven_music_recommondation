package com.musicrecommender.service;

import com.musicrecommender.model.MoodScore;
import com.musicrecommender.model.MusicCategory;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Recommendation engine that maps moods to music categories
 */
@Service
public class RecommendationEngineService {
    
    // Music category database
    private static final Map<String, MusicCategory> MUSIC_CATEGORIES = new HashMap<>();
    
    static {
        // Lo-Fi Beats
        MUSIC_CATEGORIES.put("lofi", new MusicCategory(
            "Lo-Fi Beats",
            "Chill, relaxed beats perfect for studying and focus",
            0.0,
            Arrays.asList("Lofi Girl", "ChilledCow", "Jinsang", "Idealism"),
            Arrays.asList("Lofi Hip Hop", "Study Beats", "Chill Vibes", "Focus Music")
        ));
        
        // Electronic/EDM
        MUSIC_CATEGORIES.put("electronic", new MusicCategory(
            "Electronic/EDM",
            "High-energy electronic music for workouts and parties",
            0.0,
            Arrays.asList("Avicii", "The Chainsmokers", "Calvin Harris", "Martin Garrix"),
            Arrays.asList("Wake Me Up", "Closer", "Summer", "Animals")
        ));
        
        // Jazz
        MUSIC_CATEGORIES.put("jazz", new MusicCategory(
            "Jazz",
            "Smooth jazz for relaxation and background ambiance",
            0.0,
            Arrays.asList("Miles Davis", "John Coltrane", "Bill Evans", "Duke Ellington"),
            Arrays.asList("Kind of Blue", "Blue Train", "Waltz for Debby", "Take the A Train")
        ));
        
        // Classical
        MUSIC_CATEGORIES.put("classical", new MusicCategory(
            "Classical",
            "Classical music for deep focus and concentration",
            0.0,
            Arrays.asList("Mozart", "Beethoven", "Bach", "Chopin"),
            Arrays.asList("Eine kleine Nachtmusik", "Moonlight Sonata", "Air on G String", "Nocturne")
        ));
        
        // Ambient
        MUSIC_CATEGORIES.put("ambient", new MusicCategory(
            "Ambient",
            "Atmospheric sounds for relaxation and meditation",
            0.0,
            Arrays.asList("Brian Eno", "Aphex Twin", "Boards of Canada", "Tim Hecker"),
            Arrays.asList("Music for Airports", "Selected Ambient Works", "Geogaddi", "Harmony in Ultraviolet")
        ));
        
        // Indie/Folk
        MUSIC_CATEGORIES.put("indie", new MusicCategory(
            "Indie/Folk",
            "Chill indie and folk music for casual listening",
            0.0,
            Arrays.asList("Bon Iver", "Fleet Foxes", "Iron & Wine", "Sufjan Stevens"),
            Arrays.asList("Holocene", "White Winter Hymnal", "Naked as We Came", "Chicago")
        ));
        
        // Rock
        MUSIC_CATEGORIES.put("rock", new MusicCategory(
            "Rock",
            "Energetic rock music for motivation and energy",
            0.0,
            Arrays.asList("The Beatles", "Led Zeppelin", "Queen", "AC/DC"),
            Arrays.asList("Hey Jude", "Stairway to Heaven", "Bohemian Rhapsody", "Thunderstruck")
        ));
        
        // Hip-Hop
        MUSIC_CATEGORIES.put("hiphop", new MusicCategory(
            "Hip-Hop",
            "Hip-hop beats for energy and motivation",
            0.0,
            Arrays.asList("Kendrick Lamar", "J. Cole", "Drake", "Travis Scott"),
            Arrays.asList("HUMBLE.", "No Role Modelz", "God's Plan", "SICKO MODE")
        ));
    }
    
    /**
     * Gets music category recommendations based on mood
     */
    public List<MusicCategory> getRecommendations(MoodScore moodScore) {
        String primaryMood = moodScore.getPrimaryMood().toLowerCase();
        Map<String, Double> moodDistribution = moodScore.getMoodDistribution();
        
        // Mood to category mapping
        Map<String, List<String>> moodToCategories = new HashMap<>();
        moodToCategories.put("tired", Arrays.asList("lofi", "ambient", "jazz", "classical"));
        moodToCategories.put("stressed", Arrays.asList("lofi", "ambient", "classical", "indie"));
        moodToCategories.put("energetic", Arrays.asList("electronic", "rock", "hiphop"));
        moodToCategories.put("relaxed", Arrays.asList("jazz", "ambient", "indie", "lofi"));
        moodToCategories.put("focused", Arrays.asList("classical", "lofi", "ambient", "jazz"));
        moodToCategories.put("anxious", Arrays.asList("ambient", "classical", "lofi", "jazz"));
        
        // Get categories for primary mood
        List<String> categoryKeys = moodToCategories.getOrDefault(primaryMood, Arrays.asList("lofi", "ambient"));
        
        // Create recommendations with relevance scores
        List<MusicCategory> recommendations = new ArrayList<>();
        
        for (String categoryKey : categoryKeys) {
            MusicCategory category = MUSIC_CATEGORIES.get(categoryKey);
            if (category != null) {
                // Calculate relevance score based on mood distribution
                double relevanceScore = calculateRelevanceScore(categoryKey, moodDistribution, primaryMood);
                
                MusicCategory recommendation = new MusicCategory(
                    category.getCategoryName(),
                    category.getDescription(),
                    relevanceScore,
                    category.getExampleArtists(),
                    category.getExampleTracks()
                );
                
                recommendations.add(recommendation);
            }
        }
        
        // Sort by relevance score (descending)
        recommendations.sort((a, b) -> Double.compare(b.getRelevanceScore(), a.getRelevanceScore()));
        
        // Return top 3-4 recommendations
        return recommendations.stream()
            .limit(4)
            .collect(Collectors.toList());
    }
    
    /**
     * Calculates relevance score for a category based on mood
     */
    private double calculateRelevanceScore(String categoryKey, Map<String, Double> moodDistribution, String primaryMood) {
        double baseScore = 0.5;
        
        // Boost score if category matches primary mood well
        Map<String, Double> categoryMoodFit = new HashMap<>();
        categoryMoodFit.put("lofi", 0.9); // Fits tired, stressed, focused, relaxed
        categoryMoodFit.put("ambient", 0.85); // Fits anxious, stressed, relaxed
        categoryMoodFit.put("classical", 0.8); // Fits focused, stressed
        categoryMoodFit.put("jazz", 0.75); // Fits relaxed, focused
        categoryMoodFit.put("electronic", 0.9); // Fits energetic
        categoryMoodFit.put("rock", 0.85); // Fits energetic
        categoryMoodFit.put("hiphop", 0.8); // Fits energetic
        categoryMoodFit.put("indie", 0.7); // Fits relaxed
        
        baseScore = categoryMoodFit.getOrDefault(categoryKey, 0.5);
        
        // Adjust based on mood distribution
        if (moodDistribution.containsKey(primaryMood)) {
            baseScore *= (0.5 + moodDistribution.get(primaryMood) * 0.5);
        }
        
        return Math.min(1.0, baseScore);
    }
    
    /**
     * Generates reasoning explanation for recommendations
     */
    public String generateReasoning(MoodScore moodScore, List<MusicCategory> recommendations) {
        StringBuilder reasoning = new StringBuilder();
        
        reasoning.append("Based on your input, we detected a ");
        reasoning.append(moodScore.getPrimaryMood().toLowerCase());
        reasoning.append(" mood (confidence: ");
        reasoning.append(String.format("%.0f%%", moodScore.getConfidence() * 100));
        reasoning.append("). ");
        
        if (!recommendations.isEmpty()) {
            reasoning.append("We recommend ");
            reasoning.append(recommendations.get(0).getCategoryName());
            reasoning.append(" as it's perfect for ");
            
            String mood = moodScore.getPrimaryMood().toLowerCase();
            switch (mood) {
                case "tired":
                    reasoning.append("relaxing and unwinding after a long day.");
                    break;
                case "stressed":
                    reasoning.append("calming your mind and reducing anxiety.");
                    break;
                case "energetic":
                    reasoning.append("keeping your energy levels high and staying motivated.");
                    break;
                case "focused":
                    reasoning.append("maintaining concentration and productivity.");
                    break;
                case "relaxed":
                    reasoning.append("maintaining a peaceful and calm state.");
                    break;
                case "anxious":
                    reasoning.append("soothing your nerves and promoting relaxation.");
                    break;
                default:
                    reasoning.append("enhancing your current mood.");
            }
        }
        
        return reasoning.toString();
    }
}

