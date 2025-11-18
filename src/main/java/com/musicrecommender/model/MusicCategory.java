package com.musicrecommender.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MusicCategory {
    private String categoryName; // e.g., "Lo-Fi Beats", "Electronic", "Jazz"
    private String description;
    private Double relevanceScore; // How well it matches the mood
    private List<String> exampleArtists;
    private List<String> exampleTracks;
}

