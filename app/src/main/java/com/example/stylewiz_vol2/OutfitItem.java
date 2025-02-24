package com.example.stylewiz_vol2;

public class OutfitItem {
    private String documentId;
    private String category;
    private String styleTag;
    private String season;
    private String photoUrl;

    public OutfitItem() {

    }


    public String getDocumentId() {
        return documentId;
    }
    public String getCategory() {
        return category;
    }
    public String getStyleTag() {
        return styleTag;
    }
    public String getSeason() {
        return season;
    }
    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setDocumentId(String documentId) {

        this.documentId = documentId;
    }

    public OutfitItem(String dataId, String dataCategory, String dataStyleTag, String dataSeason, String dataPhotoUrl) {
        this.documentId = dataId;
        this.category = dataCategory;
        this.styleTag = dataStyleTag;
        this.season = dataSeason;
        this.photoUrl = dataPhotoUrl;
    }
}

