package com.example.stylewiz_vol2;

import com.google.firebase.Timestamp;
import java.util.List;

public class FavoriteSuggestion {
    private String description;
    private List<String> imageUrls;
    private List<String> categories;

    private Timestamp timestamp;

    public FavoriteSuggestion() {}

    public FavoriteSuggestion(String description, List<String> imageUrls, List<String> categories, Timestamp timestamp) {
        this.description = description;
        this.imageUrls = imageUrls;
        this.categories = categories;
        this.timestamp = timestamp;
    }

    // 🔷 Getters
    public String getDescription() {
        return description;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }
    public List<String> getCategories() { return categories; }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }
    public void setCategories(List<String> categories) { this.categories = categories; }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
