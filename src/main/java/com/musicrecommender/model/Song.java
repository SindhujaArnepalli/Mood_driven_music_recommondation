package com.musicrecommender.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Song {
    private String title;
    private String artist;
    private String genre;
    private Integer duration; // in seconds
    private String mood;
    private Double energyLevel; // 0.0 to 1.0
}
