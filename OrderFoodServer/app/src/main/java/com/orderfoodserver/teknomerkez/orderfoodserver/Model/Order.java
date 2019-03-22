package com.orderfoodserver.teknomerkez.orderfoodserver.Model;

public class Order {
    private String productID;
    private String productName;
    private String quantity;
    private String price;
    private String discount;

    public Order(String productID, String productName, String quantity, String price, String discount) {
        this.productID = productID;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.discount = discount;
    }

    public Order() {
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }
}
