package com.example.stylewiz_vol2;

public class FavoriteImageItem {
    private String category;
    private String url;

    public FavoriteImageItem() {}

    public FavoriteImageItem(String category, String url) {
        this.category = category;
        this.url = url;
    }

    public String getCategory() {
        return category;
    }
    public String getUrl() {
        return url;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    public void setUrl(String url) {
        this.url = url;
    }
}
