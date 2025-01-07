package com.example.stylewiz_vol2;

public class DataClass {

    private String category;
    private String styleTag;
    private String color;
    private String season;
    private String description;
    private String image;
    private String documentId;


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

    public String getImage() {
        return image;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }


    public DataClass(String dataCategory, String dataStyleTag, String dataSeasonality, String dataImage){
        this.category = dataCategory;
        this.styleTag = dataStyleTag;
        this.season = dataSeasonality;
        this.image = dataImage;
    }

    
}
