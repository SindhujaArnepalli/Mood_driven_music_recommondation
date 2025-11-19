# Mood-Driven Music Recommendation Engine

A sophisticated backend system that predicts user mood and recommends music based on multiple contextual inputs. Built with Java and Spring Boot. A lightweight browser UI lives under `src/main/resources/static` so you can test the engine without extra tooling.

## 🎯 Features

- **Sentiment Analysis**: Lightweight ML-based sentiment analysis of user text input
- **Rules Engine**: Context-aware mood prediction using typing speed, time of day, and text patterns
- **Time-Context Learning**: Learns user behavior patterns over time for personalized recommendations
- **Dynamic Playlist Generation**: Creates personalized playlists based on predicted mood
- **RESTful API**: Clean REST endpoints for easy integration

## 🚀 Quick Start

### Prerequisites

- Java 17 or higher
- Maven 3.6+

### Running the Application

1. **Build the project:**
   ```bash
   mvn clean install
   ```

2. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```

3. **The API and demo UI will be available at:**
   ```
http://localhost:8080
   ```
Open that URL in your browser to use the built-in interface.

## 📡 API Endpoints

### Get Recommendations

**POST** `/api/recommendations`

Get music recommendations based on user input.

**Request Body:**
```json
{
  "textInput": "studying fr today",
  "typingSpeed": 1.5,
  "timeOfDay": "2024-01-15T02:00:00",
  "searchHistoryTags": ["study", "focus"]
}
```

**Query Parameters:**
- `userId` (optional): User ID for personalized learning
- `playlistLengthMinutes` (optional, default: 30): Length of generated playlist in minutes

**Response:**
```json
{
  "moodScore": {
    "primaryMood": "TIRED",
    "confidence": 0.85,
    "moodDistribution": {
      "tired": 0.85,
      "stressed": 0.45,
      "focused": 0.30
    }
  },
  "recommendedCategories": [
    {
      "categoryName": "Lo-Fi Beats",
      "description": "Chill, relaxed beats perfect for studying and focus",
      "relevanceScore": 0.92,
      "exampleArtists": ["Lofi Girl", "ChilledCow"],
      "exampleTracks": ["Lofi Hip Hop", "Study Beats"]
    }
  ],
  "playlist": {
    "playlistName": "Late Night Chill",
    "mood": "tired",
    "songs": [...],
    "totalDuration": 1800
  },
  "reasoning": "Based on your input, we detected a tired mood..."
}
```

### Predict Mood Only

**POST** `/api/recommendations/mood`

Get mood prediction without recommendations.

**Request Body:** Same as above

**Query Parameters:**
- `userId` (optional): User ID for personalized learning

### Health Check

**GET** `/api/recommendations/health`

Check if the service is running.

## 🧠 How It Works

### 1. Sentiment Analysis
- Analyzes text input for positive/negative keywords
- Detects stress, focus, and energy indicators
- Uses keyword matching and scoring algorithms

### 2. Rules Engine
- **Typing Speed**: Slow typing → tired/stressed, Fast typing → energetic
- **Time of Day**: Late night → tired, Morning → energetic, Evening → relaxed
- **Text Patterns**: Short text → tired, Study keywords → focused

### 3. Time-Context Learning
- Tracks user behavior patterns by hour of day
- Learns preferred moods at different times
- Adjusts predictions based on historical data

### 4. Mood Prediction
- Combines sentiment analysis, rules, and learned patterns
- Generates mood distribution scores
- Determines primary mood with confidence level

### 5. Recommendation Engine
- Maps moods to music categories
- Calculates relevance scores
- Returns top 3-4 recommendations

### 6. Playlist Generation
- Dynamically creates playlists from recommended categories
- Adjusts length based on user preference
- Shuffles songs for variety

## 📊 Example Scenarios

### Scenario 1: Late Night Studying
```
Input: "studying fr today"
Typing Speed: 1.5 chars/sec
Time: 2:00 AM

Result: 
- Mood: TIRED/STRESSED
- Recommendation: Lo-Fi Beats, Ambient
- Reasoning: Late night + slow typing + study keywords
```

### Scenario 2: Morning Energy
```
Input: "let's go workout!"
Typing Speed: 6.0 chars/sec
Time: 8:00 AM

Result:
- Mood: ENERGETIC
- Recommendation: Electronic, Rock, Hip-Hop
- Reasoning: Fast typing + energy keywords + morning
```

## 🛠️ Technology Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Web** (REST API)
- **Spring Data JPA** (for future persistence)
- **H2 Database** (in-memory)
- **Lombok** (reducing boilerplate)
- **Apache Commons Math** (for ML calculations)

## 📁 Project Structure

```
src/
├── main/
│   ├── java/com/musicrecommender/
│   │   ├── controller/          # REST API endpoints
│   │   ├── service/             # Business logic
│   │   │   ├── SentimentAnalysisService
│   │   │   ├── RulesEngineService
│   │   │   ├── TimeContextLearningService
│   │   │   ├── MoodPredictionService
│   │   │   ├── RecommendationEngineService
│   │   │   └── PlaylistGeneratorService
│   │   └── model/               # Data models
│   └── resources/
│       └── application.properties
└── test/
```

## 🎓 Resume Effect

> "This kid built a baby Spotify brain."

This project demonstrates:
- **ML/AI Integration**: Sentiment analysis and pattern learning
- **Complex System Design**: Multiple services working together
- **RESTful API Design**: Clean, well-structured endpoints
- **Context-Aware Computing**: Time, typing patterns, and behavior analysis
- **Dynamic Content Generation**: Playlist creation based on real-time analysis

## 🔮 Future Enhancements

- Integration with Spotify/Apple Music APIs
- Machine learning model training on user feedback
- Database persistence for user behavior
- Real-time streaming recommendations
- Multi-user support with authentication
- Advanced NLP for better sentiment analysis

## 📝 License

This project is open source and available for educational purposes.

