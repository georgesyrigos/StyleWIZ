package com.example.stylewiz_vol2;

import java.util.List;

public class OutfitSuggestion {
    private String title;
    private String description;
    private List<String> imageUrls; // or List<String> if using URIs/URLs

    public OutfitSuggestion(String title, String description, List<String> imageUrls) {
        this.title = title;
        this.description = description;
        this.imageUrls = imageUrls;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }
}

