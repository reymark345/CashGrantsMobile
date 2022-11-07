package com.example.cashgrantsmobile.Inventory;

public class Inventory {
    private int id;
    private String name;
    private String price;
    private String seriesNumber;
    private byte[] card_image;
    private byte[] grantee_image;
    private int status;
    private String hhNumber;

//    public Inventory(String name,  String price, String seriesNumber, byte[] image, byte[] idImage, int status, int id,String hhNumber) {
//        this.name = name;
//        this.price = price;
//        this.seriesNumber = seriesNumber;
//        this.image = image;
//        this.idImage = idImage;
//        this.status = status;
//        this.id = id;
//        this.hhNumber = hhNumber;
//    }

    public Inventory(String name, String price, String seriesNumber,byte[] card_image, byte[] grantee_image, int id,String hhNumber) {
        this.name = name;
        this.price = price;
        this.seriesNumber = seriesNumber;
        this.card_image = card_image;
        this.grantee_image = grantee_image;
        this.status = status;
        this.id = id;
        this.hhNumber = hhNumber;
    }

    public int getId() {
        return id;
    }

    public int getEmvId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSeriesNumber() {
        return seriesNumber;
    }

    public void setgetSeriesNumber(String seriesNumber) {
        this.seriesNumber = seriesNumber;
    }

    public byte[] getCardImage() {
        return card_image;
    }

    public void setCardImage(byte[] card_image) {
        this.card_image = card_image;
    }

    public byte[] getGranteeImage() {
        return grantee_image;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(String name) {
        this.status = status;
    }

    public void setGranteeImage(byte[] grantee_image) {
        this.grantee_image = grantee_image;
    }

    public String gethhNumber() { return hhNumber;}

    public void sethhNumber(String hhNumber) {this.hhNumber = hhNumber;}
}
