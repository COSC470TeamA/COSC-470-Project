package com.christopheramazurgmail.rtracker;

import java.util.ArrayList;

/**
 * Created by haunter on 26/10/16.
 */
public class Item {
    private String desc;
    private double price;

    public Item(String desc, double price) {
        this.desc = desc;
        this.price = price;
    }
    public Item(String desc) {
        this.desc = desc;
        this.price = 0;
    }




    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPrice() {
        return String.format("%.2f", price);
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
