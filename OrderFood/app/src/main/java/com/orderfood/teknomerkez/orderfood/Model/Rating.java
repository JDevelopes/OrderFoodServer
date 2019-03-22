package com.orderfood.teknomerkez.orderfood.Model;

public class Rating {

    private String userInfo;
    private String foodId;
    private String rateValue;
    private String comment;

    public Rating() {
    }

    public Rating(String userInfo, String foodId, String rateValue, String comment) {
        this.userInfo = userInfo;
        this.foodId = foodId;
        this.rateValue = rateValue;
        this.comment = comment;
    }

    public String getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(String userInfo) {
        this.userInfo = userInfo;
    }

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    public String getRateValue() {
        return rateValue;
    }

    public void setRateValue(String rateValue) {
        this.rateValue = rateValue;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
