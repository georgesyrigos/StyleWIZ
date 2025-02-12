package com.example.stylewiz_vol2;

public class DataClass {
    private String category;
    private String styleTag;
    private String color;
    private String season;
    private String description;
    private String photoUrl;
    private String documentId;
    private boolean liked;


    public DataClass() {

    }


    public String getCategory() {

        return category;
    }

    public String getStyleTag() {

        return styleTag;
    }

    public String getColor() {

        return color;
    }

    public String getSeason() {

        return season;
    }

    public String getDescription() {

        return description;
    }

    public String getPhotoUrl() {

        return photoUrl;
    }

    //item id
    public String getDocumentId() {

        return documentId;
    }

    public void setDocumentId(String documentId) {

        this.documentId = documentId;
    }
    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }


    public DataClass(String dataCategory, String dataStyleTag, String dataSeasonality, String dataImage){
        this.category = dataCategory;
        this.styleTag = dataStyleTag;
        this.season = dataSeasonality;
        this.photoUrl = dataImage;
    }

    
}
