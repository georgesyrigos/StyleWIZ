package com.example.stylewiz_vol2;

import com.google.firebase.Timestamp;
import java.util.List;

public class FavoriteSuggestion {
    private String description;
    private List<FavoriteImageItem> images;
    private String outfitHash;
    private Timestamp timestamp;

    public FavoriteSuggestion() {}

    public FavoriteSuggestion(String description, List<FavoriteImageItem> images, Timestamp timestamp, String outfitHash) {
        this.description = description;
        this.images = images;
        this.timestamp = timestamp;
        this.outfitHash = outfitHash;
    }

    public String getDescription() {
        return description;
    }

    public List<FavoriteImageItem> getImages() {
        return images;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getOutfitHash() {
        return outfitHash;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImages(List<FavoriteImageItem> images) {
        this.images = images;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
    public void setOutfitHash(String outfitHash) { this.outfitHash = outfitHash; }

}
