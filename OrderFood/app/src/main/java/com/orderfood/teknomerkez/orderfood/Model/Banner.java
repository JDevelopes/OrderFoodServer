package com.orderfood.teknomerkez.orderfood.Model;

public class Banner {
    private String categoryID;
    private String foodId;
    private String image;
    private String name;

    public Banner(String foodId,String categoryID,String name, String image ) {
        this.foodId = foodId;
        this.image = image;
        this.name = name;
        this.categoryID = categoryID;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }

    public Banner() {
    }

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
