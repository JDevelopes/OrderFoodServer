package com.orderfoodserver.teknomerkez.orderfoodserver.Model;

public class Banner {
    private String name,image,foodId,categoryID;

    public Banner(String name, String image, String foodId, String categoryID) {
        this.name = name;
        this.image = image;
        this.foodId = foodId;
        this.categoryID = categoryID;
    }

    public Banner() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }
}
