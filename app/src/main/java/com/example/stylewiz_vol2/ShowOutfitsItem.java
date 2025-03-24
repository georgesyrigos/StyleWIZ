package com.example.stylewiz_vol2;

public class ShowOutfitsItem {
    private String top1, top2, bottom, onePiece, layerOnePiece, shoes, accessory1, accessory2, outerwear;
    private String documentId; // Firestore document ID for reference

    public ShowOutfitsItem() {
        // Empty constructor needed for Firestore
    }


    // Getters
    public String getTop1() { return top1; }
    public String getTop2() { return top2; }
    public String getBottom() { return bottom; }
    public String getOnePiece() { return onePiece; }
    public String getLayerOnePiece() { return layerOnePiece; }
    public String getShoes() { return shoes; }
    public String getAccessory1() { return accessory1; }
    public String getAccessory2() { return accessory2; }
    public String getOuterwear() { return outerwear; }
    public String getDocumentId() { return documentId; }
    public void setDocumentId(String documentId) {

        this.documentId = documentId;
    }

    public ShowOutfitsItem(String top1, String top2, String bottom, String onePiece, String layerOnePiece, String shoes,
                           String accessory1, String accessory2, String outerwear, String documentId) {
        this.top1 = top1;
        this.top2 = top2;
        this.bottom = bottom;
        this.onePiece = onePiece;
        this.layerOnePiece = layerOnePiece;
        this.shoes = shoes;
        this.accessory1 = accessory1;
        this.accessory2 = accessory2;
        this.outerwear = outerwear;
        this.documentId = documentId;
    }
}

