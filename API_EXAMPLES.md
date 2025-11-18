# API Usage Examples

## Example 1: Late Night Studying (Tired/Stressed Mood)

```bash
curl -X POST http://localhost:8080/api/recommendations?userId=user123&playlistLengthMinutes=30 \
  -H "Content-Type: application/json" \
  -d '{
    "textInput": "studying fr today",
    "typingSpeed": 1.5,
    "timeOfDay": "2024-01-15T02:00:00",
    "searchHistoryTags": ["study", "focus"]
  }'
```

**Expected Result:**
- Primary Mood: TIRED or STRESSED
- Recommended Categories: Lo-Fi Beats, Ambient, Classical
- Reasoning: Late night (2 AM) + slow typing speed (1.5) + study keywords → tired/stressed mood

---

## Example 2: Morning Energy (Energetic Mood)

```bash
curl -X POST http://localhost:8080/api/recommendations?userId=user123 \
  -H "Content-Type: application/json" \
  -d '{
    "textInput": "let'\''s go workout!",
    "typingSpeed": 6.0,
    "timeOfDay": "2024-01-15T08:00:00",
    "searchHistoryTags": ["workout", "energy"]
  }'
```

**Expected Result:**
- Primary Mood: ENERGETIC
- Recommended Categories: Electronic/EDM, Rock, Hip-Hop
- Reasoning: Fast typing + energy keywords + morning time → energetic mood

---

## Example 3: Evening Relaxation (Relaxed Mood)

```bash
curl -X POST http://localhost:8080/api/recommendations \
  -H "Content-Type: application/json" \
  -d '{
    "textInput": "just finished work, time to chill",
    "typingSpeed": 3.5,
    "timeOfDay": "2024-01-15T19:00:00",
    "searchHistoryTags": ["relax", "jazz"]
  }'
```

**Expected Result:**
- Primary Mood: RELAXED
- Recommended Categories: Jazz, Ambient, Indie/Folk
- Reasoning: Evening time + relaxed keywords → relaxed mood

---

## Example 4: Focus Mode (Focused Mood)

```bash
curl -X POST http://localhost:8080/api/recommendations?playlistLengthMinutes=60 \
  -H "Content-Type: application/json" \
  -d '{
    "textInput": "need to focus on this project",
    "typingSpeed": 4.0,
    "timeOfDay": "2024-01-15T14:00:00",
    "searchHistoryTags": ["focus", "productivity", "study"]
  }'
```

**Expected Result:**
- Primary Mood: FOCUSED
- Recommended Categories: Classical, Lo-Fi Beats, Ambient
- Reasoning: Focus keywords + afternoon → focused mood

---

## Example 5: Mood Prediction Only (No Recommendations)

```bash
curl -X POST http://localhost:8080/api/recommendations/mood?userId=user123 \
  -H "Content-Type: application/json" \
  -d '{
    "textInput": "feeling anxious about tomorrow",
    "typingSpeed": 2.5,
    "timeOfDay": "2024-01-15T22:30:00",
    "searchHistoryTags": ["anxiety", "calm"]
  }'
```

**Expected Result:**
- Primary Mood: ANXIOUS or STRESSED
- Confidence: High (based on keywords and time)

---

## Health Check

```bash
curl http://localhost:8080/api/recommendations/health
```

**Expected Response:**
```json
{
  "status": "UP",
  "service": "Mood-Driven Music Recommendation Engine"
}
```

---

## Using Postman or Similar Tools

1. **Method:** POST
2. **URL:** `http://localhost:8080/api/recommendations`
3. **Headers:**
   - `Content-Type: application/json`
4. **Body (raw JSON):**
   ```json
   {
     "textInput": "your text here",
     "typingSpeed": 3.0,
     "timeOfDay": "2024-01-15T12:00:00",
     "searchHistoryTags": ["tag1", "tag2"]
   }
   ```
5. **Query Parameters (optional):**
   - `userId`: user123
   - `playlistLengthMinutes`: 30

---

## Notes

- **Typing Speed**: Measured in characters per second (typical range: 1.0 - 8.0)
- **Time Format**: ISO 8601 format (e.g., "2024-01-15T14:30:00")
- **Search History Tags**: Optional array of strings
- **User ID**: Optional, but recommended for personalized learning
- **Playlist Length**: Default is 30 minutes, can be customized

