package com.orderfood.teknomerkez.orderfood.Model;

import com.google.firebase.database.Exclude;

public class Address {

    public String homeAddress;
    public String workAdress;
    public String displayName;

    public Address() {
    }

    @Exclude
    public String getHomeAddress() {
        return homeAddress;
    }

    @Exclude
    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }

    @Exclude
    public String getWorkAdress() {
        return this.workAdress;
    }

    @Exclude
    public void setWorkAdress(String workAdress) {
        this.workAdress = workAdress;
    }

    @Exclude
    public String getDisplayName() {
        return this.displayName;
    }

    @Exclude
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
