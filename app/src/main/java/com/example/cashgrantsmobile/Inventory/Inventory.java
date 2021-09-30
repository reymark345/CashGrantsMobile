package com.example.cashgrantsmobile.Inventory;

public class Inventory {
    private int id;
    private String name;
    private String price;
    private String seriesNumber;
    private byte[] image;
    private byte[] idImage;
    private int status;

    public Inventory(String name, String price, String seriesNumber, byte[] image, byte[] idImage, int status, int id) {
        this.name = name;
        this.price = price;
        this.seriesNumber = seriesNumber;
        this.image = image;
        this.idImage = idImage;
        this.status = status;
        this.id = id;
    }

    public int getId() {
        return id;
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


    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public byte[] getIdImage() {
        return idImage;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(String name) {
        this.status = status;
    }





    public void setIdImage(byte[] idImage) {
        this.idImage = idImage;
    }
}
