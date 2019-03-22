package com.orderfood.teknomerkez.orderfood.Model;

public class Favorites {

    private String FoodId, FoodName, FoodPrice, FoodMenuId, FoodImage, FoodDiscount, FoodDescription, FoodUserId;


    public Favorites() {
    }

    public Favorites(String foodUserId, String foodId, String foodName, String foodPrice, String foodMenuId, String foodImage, String foodDiscount, String foodDescription) {
        FoodUserId = foodUserId;
        FoodId = foodId;
        FoodName = foodName;
        FoodPrice = foodPrice;
        FoodMenuId = foodMenuId;
        FoodImage = foodImage;
        FoodDiscount = foodDiscount;
        FoodDescription = foodDescription;

    }

    public String getFoodId() {
        return FoodId;
    }

    public void setFoodId(String foodId) {
        FoodId = foodId;
    }

    public String getFoodName() {
        return FoodName;
    }

    public void setFoodName(String foodName) {
        FoodName = foodName;
    }

    public String getFoodPrice() {
        return FoodPrice;
    }

    public void setFoodPrice(String foodPrice) {
        FoodPrice = foodPrice;
    }

    public String getFoodMenuId() {
        return FoodMenuId;
    }

    public void setFoodMenuId(String foodMenuId) {
        FoodMenuId = foodMenuId;
    }

    public String getFoodImage() {
        return FoodImage;
    }

    public void setFoodImage(String foodImage) {
        FoodImage = foodImage;
    }

    public String getFoodDiscount() {
        return FoodDiscount;
    }

    public void setFoodDiscount(String foodDiscount) {
        FoodDiscount = foodDiscount;
    }

    public String getFoodDescription() {
        return FoodDescription;
    }

    public void setFoodDescription(String foodDescription) {
        FoodDescription = foodDescription;
    }

    public String getFoodUserId() {
        return FoodUserId;
    }

    public void setFoodUserId(String foodUserId) {
        FoodUserId = foodUserId;
    }
}
