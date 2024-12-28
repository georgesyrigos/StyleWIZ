package com.example.stylewiz_vol2;

public class DataClass {

    private String category;
    private String styleTag;
    private String color;
    private String season;
    private String description;
    private String image;

    public DataClass() {

    }

    public String getCategory() {
        return category;
    }

    public String getStyleTag() {
        return styleTag;
    }

    /*public String getDataColor() {
        return dataColor;
    }*/

    public String getSeason() {
        return season;
    }

    /*public String getDataDescription() {
        return dataDescription;
    }*/

    public String getImage() {
        return image;
    }

    //public DataClass(String dataCategory, String dataStyleTag, String dataColor, String dataSeasonality, String dataDescription, String dataImage){

    public DataClass(String dataCategory, String dataStyleTag, String dataSeasonality, String dataImage){
        this.category = dataCategory;
        this.styleTag = dataStyleTag;
        //this.dataColor = dataColor;
        this.season = dataSeasonality;
        //this.dataDescription = dataDescription;
        this.image = dataImage;
    }

    
}
