package com.example.stylewiz_vol2;
public class ImageItem {
    private String url;
    private String category;

    public ImageItem() {}

    public ImageItem(String url, String category) {
        this.url = url;
        this.category = category;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
