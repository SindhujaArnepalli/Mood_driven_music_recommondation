package com.musicrecommender.service;

import com.musicrecommender.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Generates dynamic playlists based on mood and recommendations
 */
@Service
public class PlaylistGeneratorService {
    
    @Autowired
    private RecommendationEngineService recommendationEngineService;
    
    // Song database (in production, this would come from a music API)
    private static final Map<String, List<Song>> SONGS_BY_CATEGORY = new HashMap<>();
    
    static {
        // Lo-Fi songs
        SONGS_BY_CATEGORY.put("lofi", Arrays.asList(
            new Song("Midnight City", "Lofi Girl", "Lo-Fi", 180, "relaxed", 0.3),
            new Song("Study Session", "ChilledCow", "Lo-Fi", 200, "focused", 0.2),
            new Song("Coffee Break", "Jinsang", "Lo-Fi", 175, "relaxed", 0.25),
            new Song("Rainy Day", "Idealism", "Lo-Fi", 190, "calm", 0.2),
            new Song("Late Night", "Kupla", "Lo-Fi", 185, "tired", 0.15)
        ));
        
        // Electronic songs
        SONGS_BY_CATEGORY.put("electronic", Arrays.asList(
            new Song("Wake Me Up", "Avicii", "Electronic", 247, "energetic", 0.9),
            new Song("Closer", "The Chainsmokers", "Electronic", 244, "energetic", 0.85),
            new Song("Summer", "Calvin Harris", "Electronic", 223, "happy", 0.8),
            new Song("Animals", "Martin Garrix", "Electronic", 195, "energetic", 0.95),
            new Song("Levels", "Avicii", "Electronic", 202, "energetic", 0.9)
        ));
        
        // Jazz songs
        SONGS_BY_CATEGORY.put("jazz", Arrays.asList(
            new Song("Kind of Blue", "Miles Davis", "Jazz", 345, "relaxed", 0.4),
            new Song("Blue Train", "John Coltrane", "Jazz", 420, "focused", 0.5),
            new Song("Waltz for Debby", "Bill Evans", "Jazz", 380, "relaxed", 0.3),
            new Song("Take the A Train", "Duke Ellington", "Jazz", 280, "happy", 0.6),
            new Song("So What", "Miles Davis", "Jazz", 320, "relaxed", 0.4)
        ));
        
        // Classical songs
        SONGS_BY_CATEGORY.put("classical", Arrays.asList(
            new Song("Eine kleine Nachtmusik", "Mozart", "Classical", 360, "focused", 0.5),
            new Song("Moonlight Sonata", "Beethoven", "Classical", 900, "relaxed", 0.3),
            new Song("Air on G String", "Bach", "Classical", 240, "calm", 0.2),
            new Song("Nocturne Op.9 No.2", "Chopin", "Classical", 280, "relaxed", 0.25),
            new Song("Clair de Lune", "Debussy", "Classical", 300, "calm", 0.2)
        ));
        
        // Ambient songs
        SONGS_BY_CATEGORY.put("ambient", Arrays.asList(
            new Song("Music for Airports", "Brian Eno", "Ambient", 1200, "calm", 0.1),
            new Song("Selected Ambient Works", "Aphex Twin", "Ambient", 420, "relaxed", 0.15),
            new Song("Geogaddi", "Boards of Canada", "Ambient", 380, "calm", 0.2),
            new Song("Harmony in Ultraviolet", "Tim Hecker", "Ambient", 450, "relaxed", 0.15),
            new Song("Disintegration Loops", "William Basinski", "Ambient", 3600, "calm", 0.1)
        ));
        
        // Indie songs
        SONGS_BY_CATEGORY.put("indie", Arrays.asList(
            new Song("Holocene", "Bon Iver", "Indie", 320, "relaxed", 0.4),
            new Song("White Winter Hymnal", "Fleet Foxes", "Indie", 180, "calm", 0.3),
            new Song("Naked as We Came", "Iron & Wine", "Indie", 200, "relaxed", 0.35),
            new Song("Chicago", "Sufjan Stevens", "Indie", 380, "happy", 0.5),
            new Song("Skinny Love", "Bon Iver", "Indie", 240, "sad", 0.3)
        ));
        
        // Rock songs
        SONGS_BY_CATEGORY.put("rock", Arrays.asList(
            new Song("Hey Jude", "The Beatles", "Rock", 431, "happy", 0.7),
            new Song("Stairway to Heaven", "Led Zeppelin", "Rock", 482, "energetic", 0.8),
            new Song("Bohemian Rhapsody", "Queen", "Rock", 355, "energetic", 0.85),
            new Song("Thunderstruck", "AC/DC", "Rock", 292, "energetic", 0.95),
            new Song("Sweet Child O' Mine", "Guns N' Roses", "Rock", 356, "happy", 0.75)
        ));
        
        // Hip-Hop songs
        SONGS_BY_CATEGORY.put("hiphop", Arrays.asList(
            new Song("HUMBLE.", "Kendrick Lamar", "Hip-Hop", 177, "energetic", 0.9),
            new Song("No Role Modelz", "J. Cole", "Hip-Hop", 289, "focused", 0.7),
            new Song("God's Plan", "Drake", "Hip-Hop", 198, "happy", 0.8),
            new Song("SICKO MODE", "Travis Scott", "Hip-Hop", 312, "energetic", 0.95),
            new Song("Money Trees", "Kendrick Lamar", "Hip-Hop", 386, "focused", 0.75)
        ));
    }
    
    /**
     * Generates a dynamic playlist based on mood and recommendations
     */
    public Playlist generatePlaylist(MoodScore moodScore, List<MusicCategory> recommendations, int playlistLength) {
        if (recommendations.isEmpty()) {
            return createDefaultPlaylist(moodScore);
        }
        
        String primaryMood = moodScore.getPrimaryMood().toLowerCase();
        String playlistName = generatePlaylistName(primaryMood);
        
        // Get songs from top recommended categories
        List<Song> playlistSongs = new ArrayList<>();
        int targetDuration = playlistLength * 60; // Convert minutes to seconds
        int currentDuration = 0;
        
        // Prioritize top recommendation
        MusicCategory topCategory = recommendations.get(0);
        String categoryKey = getCategoryKey(topCategory.getCategoryName());
        List<Song> categorySongs = SONGS_BY_CATEGORY.getOrDefault(categoryKey, new ArrayList<>());
        
        // Add songs from top category
        for (Song song : categorySongs) {
            if (currentDuration >= targetDuration) break;
            playlistSongs.add(song);
            currentDuration += song.getDuration();
        }
        
        // Fill remaining time with songs from other recommended categories
        for (int i = 1; i < recommendations.size() && currentDuration < targetDuration; i++) {
            categoryKey = getCategoryKey(recommendations.get(i).getCategoryName());
            categorySongs = SONGS_BY_CATEGORY.getOrDefault(categoryKey, new ArrayList<>());
            
            for (Song song : categorySongs) {
                if (currentDuration >= targetDuration) break;
                playlistSongs.add(song);
                currentDuration += song.getDuration();
            }
        }
        
        // Shuffle playlist for variety
        Collections.shuffle(playlistSongs);
        
        return new Playlist(playlistName, primaryMood, playlistSongs, currentDuration);
    }
    
    /**
     * Generates a playlist name based on mood
     */
    private String generatePlaylistName(String mood) {
        Map<String, String> moodNames = new HashMap<>();
        moodNames.put("tired", "Late Night Chill");
        moodNames.put("stressed", "Stress Relief");
        moodNames.put("energetic", "Energy Boost");
        moodNames.put("relaxed", "Relaxation Station");
        moodNames.put("focused", "Deep Focus");
        moodNames.put("anxious", "Calm & Collected");
        
        return moodNames.getOrDefault(mood, "Mood Playlist");
    }
    
    /**
     * Gets category key from category name
     */
    private String getCategoryKey(String categoryName) {
        Map<String, String> nameToKey = new HashMap<>();
        nameToKey.put("Lo-Fi Beats", "lofi");
        nameToKey.put("Electronic/EDM", "electronic");
        nameToKey.put("Jazz", "jazz");
        nameToKey.put("Classical", "classical");
        nameToKey.put("Ambient", "ambient");
        nameToKey.put("Indie/Folk", "indie");
        nameToKey.put("Rock", "rock");
        nameToKey.put("Hip-Hop", "hiphop");
        
        return nameToKey.getOrDefault(categoryName, "lofi");
    }
    
    /**
     * Creates a default playlist if no recommendations
     */
    private Playlist createDefaultPlaylist(MoodScore moodScore) {
        List<Song> defaultSongs = SONGS_BY_CATEGORY.get("lofi").subList(0, 5);
        return new Playlist("Default Playlist", moodScore.getPrimaryMood().toLowerCase(), 
                           defaultSongs, defaultSongs.stream().mapToInt(Song::getDuration).sum());
    }
}

