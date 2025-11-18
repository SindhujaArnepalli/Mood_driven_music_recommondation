package com.musicrecommender.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Playlist {
    private String playlistName;
    private String mood;
    private List<Song> songs;
    private Integer totalDuration; // in seconds
}

