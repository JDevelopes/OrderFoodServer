package com.orderfoodserver.teknomerkez.orderfoodserver.Model;

public class Category {

    private String name;
    public String image;

    public Category(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public Category() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
