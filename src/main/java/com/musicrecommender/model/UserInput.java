package com.musicrecommender.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInput {
    @NotBlank(message = "Text input cannot be blank")
    private String textInput;
    
    @NotNull(message = "Typing speed cannot be null")
    @Min(value = 0, message = "Typing speed must be positive")
    private Double typingSpeed; // characters per second
    
    @NotNull(message = "Time of day cannot be null")
    private LocalDateTime timeOfDay;
    
    private List<String> searchHistoryTags; // Optional search history
    
    // Helper method to get hour of day
    public int getHourOfDay() {
        return timeOfDay.getHour();
    }
}

