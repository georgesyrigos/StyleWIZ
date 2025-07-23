package com.example.stylewiz_vol2;

import java.util.List;

public class OutfitSuggestion {
    private String title;
    private String description;
    private List<ImageItem> images;

    public OutfitSuggestion() {}

    public OutfitSuggestion(String title, String description, List<ImageItem> images) {
        this.title = title;
        this.description = description;
        this.images = images;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ImageItem> getImages() {
        return images;
    }

    public void setImages(List<ImageItem> images) {
        this.images = images;
    }

}

